package snowflake.mianjing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
Problem Summary
---------------
Design and build an in-memory message queue (SQS-style). This interview
question has 3 parts:

Part 1: Basic queue
  createQueue(name)          — create a named queue
  send(queue, body)          — enqueue a message; returns message id
  receive(queue)             — dequeue a message; returns Optional<Delivery>
  deleteMessage(receipt)     — permanently remove a message after processing
  deleteQueue(name)          — drop a queue

Part 2: Visibility timeout + receipt handles (the SQS pattern)
  - receive() returns a receipt handle and HIDES the message for
    `visibilityTimeoutMs`. Other consumers can't see it until either
    (a) deleteMessage(receipt) is called, or
    (b) the timeout expires and the message becomes visible again.
  - The receipt handle carries a generation number (fencing token).
    A stale receipt cannot delete a message — once visibility expires
    and the message is re-delivered, the old receipt is dead.
  - Optional: changeMessageVisibility(receipt, newTimeoutMs) extends
    the lease for long-running consumers.

Part 3: Multiple queues + dead-letter queue + retries + thread safety
  - Each queue tracks per-message receive count.
  - After `maxReceiveCount` failed attempts, the message is moved to a
    dead-letter queue (DLQ) for inspection.
  - Per-queue lock allows concurrent operations on different queues.

Why this is the right abstraction
---------------------------------
The visibility timeout is the production answer to "how do I make sure
two consumers don't pull the same message?" — when a consumer pulls,
the broker hides the message; if the consumer doesn't ack within the
timeout, the message comes back.

This is at-least-once delivery: a consumer crash AFTER processing but
BEFORE delete causes the message to be redelivered. Idempotent
consumer code converts at-least-once to effectively-once.

NOT implemented (out of scope for an in-memory toy):
  - Persistence / durability
  - Replication / fault tolerance
  - Partitioning / consumer groups (Kafka-style offsets)
  - FIFO with exactly-once strict ordering
*/
public class InMemoryMessageQueue {

    /* ============================== Public types ============================== */

    /** A message handed to a producer/consumer. Body is opaque. */
    public static final class Message {
        public final String id;
        public final String body;
        public final long createdAtMs;
        public final int receiveCount;

        private Message(String id, String body, long createdAtMs, int receiveCount) {
            this.id = id;
            this.body = body;
            this.createdAtMs = createdAtMs;
            this.receiveCount = receiveCount;
        }

        @Override public String toString() {
            return "Message{id=" + id + ", body=" + body + ", recv=" + receiveCount + "}";
        }
    }

    /** The result of a successful receive(): the message + the receipt handle. */
    public static final class Delivery {
        public final Message message;
        public final Receipt receipt;

        Delivery(Message message, Receipt receipt) {
            this.message = message;
            this.receipt = receipt;
        }
    }

    /**
     * Receipt handle proves "I am the consumer holding the lease right now."
     * The generation number is a fencing token: an expired/superseded
     * receipt is rejected even if the same messageId is currently in flight
     * to a different consumer.
     */
    public static final class Receipt {
        final String queueName;
        final String messageId;
        final long generation;

        Receipt(String queueName, String messageId, long generation) {
            this.queueName = queueName;
            this.messageId = messageId;
            this.generation = generation;
        }

        @Override public String toString() {
            return "Receipt{q=" + queueName + ", m=" + messageId + ", gen=" + generation + "}";
        }
    }

    /** Pluggable clock so tests can fast-forward time without sleeping. */
    public interface Clock {
        long nowMs();
    }

    public static final class TestClock implements Clock {
        private long now = 0;
        @Override public long nowMs() { return now; }
        public void advanceMs(long ms) { now += ms; }
    }

    public static final class SystemClock implements Clock {
        @Override public long nowMs() { return System.currentTimeMillis(); }
    }

    /* ============================== Internal types ============================== */

    /** A message currently being processed (hidden from receive()). */
    private static final class Inflight {
        final Message message;
        long visibleAtMs;        // when the lease expires
        final long generation;   // fencing token; bumped each redelivery
        boolean alive;           // false once delivered / moved back to visible

        Inflight(Message message, long visibleAtMs, long generation) {
            this.message = message;
            this.visibleAtMs = visibleAtMs;
            this.generation = generation;
            this.alive = true;
        }
    }

    /** A single named queue. All operations on this queue hold its lock. */
    private static final class Queue {
        final String name;
        final long defaultVisibilityTimeoutMs;
        final int maxReceiveCount;            // after this many redeliveries → DLQ
        final String dlqName;                  // null if no DLQ configured

        final Deque<Message> visible = new ArrayDeque<>();
        final Map<String, Inflight> inflight = new HashMap<>();
        // min-heap by visibleAtMs so we can lazily reclaim expired leases on receive
        final PriorityQueue<Inflight> expiryHeap =
                new PriorityQueue<>(Comparator.comparingLong(i -> i.visibleAtMs));

        Queue(String name, long visTimeoutMs, int maxReceiveCount, String dlqName) {
            this.name = name;
            this.defaultVisibilityTimeoutMs = visTimeoutMs;
            this.maxReceiveCount = maxReceiveCount;
            this.dlqName = dlqName;
        }
    }

    /* ============================== State ============================== */

    private final Map<String, Queue> queues = new ConcurrentHashMap<>();
    private final AtomicLong generationSeq = new AtomicLong(0);
    private final Clock clock;

    public InMemoryMessageQueue(Clock clock) {
        this.clock = clock;
    }

    /* ============================== Admin ops ============================== */

    public void createQueue(String name, long visibilityTimeoutMs,
                            int maxReceiveCount, String dlqName) {
        if (dlqName != null && !queues.containsKey(dlqName)) {
            throw new IllegalArgumentException("DLQ does not exist: " + dlqName);
        }
        Queue q = new Queue(name, visibilityTimeoutMs, maxReceiveCount, dlqName);
        if (queues.putIfAbsent(name, q) != null) {
            throw new IllegalStateException("queue already exists: " + name);
        }
    }

    /** Convenience: create a queue with no DLQ and unlimited redeliveries. */
    public void createQueue(String name, long visibilityTimeoutMs) {
        createQueue(name, visibilityTimeoutMs, Integer.MAX_VALUE, null);
    }

    public void deleteQueue(String name) {
        queues.remove(name);
    }

    public int approximateSize(String name) {
        Queue q = queueOrThrow(name);
        synchronized (q) {
            return q.visible.size() + q.inflight.size();
        }
    }

    /* ============================== Producer side ============================== */

    /** Append a message to the queue. Returns the new message id. */
    public String send(String queueName, String body) {
        Queue q = queueOrThrow(queueName);
        String id = UUID.randomUUID().toString();
        Message m = new Message(id, body, clock.nowMs(), 0);
        synchronized (q) {
            q.visible.addLast(m);
        }
        return id;
    }

    /* ============================== Consumer side ============================== */

    /**
     * Pull one message off the queue. Returns Optional.empty() if no message
     * is currently visible. The returned message is hidden from other
     * consumers until either deleteMessage(receipt) is called or the
     * visibility timeout expires.
     */
    public Optional<Delivery> receive(String queueName) {
        return receive(queueName, -1);
    }

    public Optional<Delivery> receive(String queueName, long visibilityOverrideMs) {
        Queue q = queueOrThrow(queueName);
        long timeout = visibilityOverrideMs > 0 ? visibilityOverrideMs : q.defaultVisibilityTimeoutMs;

        synchronized (q) {
            reapExpired(q);
            Message head = q.visible.pollFirst();
            if (head == null) return Optional.empty();

            Message redelivered = new Message(head.id, head.body, head.createdAtMs, head.receiveCount + 1);
            long generation = generationSeq.incrementAndGet();
            Inflight inf = new Inflight(redelivered, clock.nowMs() + timeout, generation);
            q.inflight.put(redelivered.id, inf);
            q.expiryHeap.offer(inf);

            return Optional.of(new Delivery(redelivered, new Receipt(q.name, redelivered.id, generation)));
        }
    }

    /** Permanently remove the message. Throws if the receipt is stale or unknown. */
    public void deleteMessage(Receipt receipt) {
        Queue q = queueOrThrow(receipt.queueName);
        synchronized (q) {
            Inflight inf = q.inflight.get(receipt.messageId);
            if (inf == null || inf.generation != receipt.generation || !inf.alive) {
                throw new IllegalStateException("stale or unknown receipt: " + receipt);
            }
            inf.alive = false;             // mark dead; expiry heap will skip it
            q.inflight.remove(receipt.messageId);
        }
    }

    /** Extend the lease on an in-flight message. */
    public void changeMessageVisibility(Receipt receipt, long newTimeoutMs) {
        Queue q = queueOrThrow(receipt.queueName);
        synchronized (q) {
            Inflight inf = q.inflight.get(receipt.messageId);
            if (inf == null || inf.generation != receipt.generation || !inf.alive) {
                throw new IllegalStateException("stale or unknown receipt: " + receipt);
            }
            inf.visibleAtMs = clock.nowMs() + newTimeoutMs;
            // Note: not re-heapifying. The heap may pop an entry "early"; reapExpired
            // re-checks visibleAtMs and re-pushes if it's not actually expired yet.
            q.expiryHeap.offer(inf);  // duplicate entry; the older one becomes a no-op
        }
    }

    /* ============================== Lease expiry / DLQ ============================== */

    /**
     * Walk the expiry heap; for each entry whose lease has expired, either
     * (a) move it back to visible (consumer must have died), or
     * (b) move it to the DLQ if it's been redelivered too many times.
     *
     * Called lazily on every receive(). A real system would also run a
     * background thread that calls this periodically so messages reappear
     * even if no one is calling receive().
     */
    private void reapExpired(Queue q) {
        long now = clock.nowMs();
        while (!q.expiryHeap.isEmpty()) {
            Inflight head = q.expiryHeap.peek();
            if (!head.alive) {                  // tombstoned by deleteMessage
                q.expiryHeap.poll();
                continue;
            }
            if (head.visibleAtMs > now) {
                break;                          // not expired yet
            }
            q.expiryHeap.poll();
            // could be a duplicate heap entry from changeMessageVisibility; re-check.
            Inflight current = q.inflight.get(head.message.id);
            if (current == null || current != head) {
                continue;                       // outdated heap entry; ignore
            }

            current.alive = false;
            q.inflight.remove(head.message.id);

            if (head.message.receiveCount >= q.maxReceiveCount && q.dlqName != null) {
                Queue dlq = queues.get(q.dlqName);
                if (dlq != null) {
                    synchronized (dlq) {
                        dlq.visible.addLast(new Message(
                                head.message.id, head.message.body,
                                head.message.createdAtMs, head.message.receiveCount));
                    }
                }
            } else {
                // Redeliver — reset receiveCount tracking for next receive().
                q.visible.addFirst(new Message(
                        head.message.id, head.message.body,
                        head.message.createdAtMs, head.message.receiveCount));
            }
        }
    }

    /* ============================== Helpers ============================== */

    private Queue queueOrThrow(String name) {
        Queue q = queues.get(name);
        if (q == null) throw new IllegalStateException("no such queue: " + name);
        return q;
    }

    /* ============================== Demo / tests ============================== */

    public static void main(String[] args) {
        TestClock clock = new TestClock();
        InMemoryMessageQueue mq = new InMemoryMessageQueue(clock);

        part1Basic(mq);
        part2VisibilityTimeout(mq, clock);
        part3DeadLetterQueue(mq, clock);
        part4MultiConsumerExclusivity(clock);

        System.out.println("All assertions passed.");
    }

    /* ----- Part 1: basic enqueue/dequeue ----- */

    private static void part1Basic(InMemoryMessageQueue mq) {
        mq.createQueue("orders", 5_000);

        mq.send("orders", "order-1");
        mq.send("orders", "order-2");

        Delivery d1 = mq.receive("orders").orElseThrow();
        check("order-1".equals(d1.message.body));

        Delivery d2 = mq.receive("orders").orElseThrow();
        check("order-2".equals(d2.message.body));

        check(mq.receive("orders").isEmpty());

        mq.deleteMessage(d1.receipt);
        mq.deleteMessage(d2.receipt);

        mq.deleteQueue("orders");
        System.out.println("Part 1 OK");
    }

    /* ----- Part 2: visibility timeout + receipt fencing ----- */

    private static void part2VisibilityTimeout(InMemoryMessageQueue mq, TestClock clock) {
        mq.createQueue("tasks", 30_000);
        mq.send("tasks", "task-A");

        // First consumer pulls; message becomes invisible.
        Delivery d = mq.receive("tasks").orElseThrow();
        check("task-A".equals(d.message.body));
        check(mq.receive("tasks").isEmpty(), "second consumer must not see in-flight msg");

        // Lease expires before consumer acks — message becomes visible again.
        clock.advanceMs(31_000);
        Delivery redelivered = mq.receive("tasks").orElseThrow();
        check("task-A".equals(redelivered.message.body));
        check(redelivered.message.receiveCount == 2, "receiveCount should increment on redelivery");

        // The original receipt is now stale (different generation) — fencing.
        boolean threw = false;
        try {
            mq.deleteMessage(d.receipt);
        } catch (IllegalStateException expected) {
            threw = true;
        }
        check(threw, "stale receipt must be rejected");

        // The new receipt acks correctly.
        mq.deleteMessage(redelivered.receipt);
        check(mq.approximateSize("tasks") == 0);

        // Lease extension keeps a long-running consumer's message hidden.
        mq.send("tasks", "long-task");
        Delivery longD = mq.receive("tasks").orElseThrow();
        clock.advanceMs(20_000);                     // < 30s default
        mq.changeMessageVisibility(longD.receipt, 60_000);
        clock.advanceMs(40_000);                     // total 60s, but extended to 60s from t=20
        check(mq.receive("tasks").isEmpty(), "extended lease must still hide the message");
        clock.advanceMs(25_000);                     // now well past extension
        Delivery reapAgain = mq.receive("tasks").orElseThrow();
        check("long-task".equals(reapAgain.message.body));
        mq.deleteMessage(reapAgain.receipt);

        mq.deleteQueue("tasks");
        System.out.println("Part 2 OK");
    }

    /* ----- Part 3: DLQ after N failed deliveries ----- */

    private static void part3DeadLetterQueue(InMemoryMessageQueue mq, TestClock clock) {
        mq.createQueue("dlq.payments", 60_000);
        mq.createQueue("payments", 5_000, /*maxReceiveCount=*/3, "dlq.payments");

        mq.send("payments", "pay-1");

        // Three failed deliveries (consumer keeps timing out).
        for (int i = 1; i <= 3; i++) {
            Delivery d = mq.receive("payments").orElseThrow();
            check(d.message.receiveCount == i);
            clock.advanceMs(6_000);   // exceed visibility, no ack
        }

        // The 4th receive on "payments" should find nothing; the message
        // moved to the DLQ on the last expiry sweep.
        check(mq.receive("payments").isEmpty(), "after maxReceiveCount, msg leaves the main queue");
        Delivery fromDlq = mq.receive("dlq.payments").orElseThrow();
        check("pay-1".equals(fromDlq.message.body));
        mq.deleteMessage(fromDlq.receipt);

        mq.deleteQueue("payments");
        mq.deleteQueue("dlq.payments");
        System.out.println("Part 3 OK");
    }

    /* ----- Part 4: many concurrent consumers, no double delivery ----- */

    private static void part4MultiConsumerExclusivity(TestClock clock) throws RuntimeException {
        InMemoryMessageQueue mq = new InMemoryMessageQueue(new SystemClock());
        mq.createQueue("work", 5_000);

        final int N = 1000;
        for (int i = 0; i < N; i++) mq.send("work", "w-" + i);

        final int CONSUMERS = 8;
        Map<String, String> processedBy = new ConcurrentHashMap<>();
        List<Thread> threads = new ArrayList<>();
        for (int c = 0; c < CONSUMERS; c++) {
            final String consumerId = "c-" + c;
            threads.add(new Thread(() -> {
                while (true) {
                    Optional<Delivery> got = mq.receive("work");
                    if (got.isEmpty()) {
                        if (mq.approximateSize("work") == 0) return;
                        Thread.yield();
                        continue;
                    }
                    Delivery d = got.get();
                    String prior = processedBy.putIfAbsent(d.message.id, consumerId);
                    if (prior != null) {
                        throw new RuntimeException("DOUBLE DELIVERY: " + d.message.id +
                                " seen by " + prior + " and " + consumerId);
                    }
                    mq.deleteMessage(d.receipt);
                }
            }));
        }
        threads.forEach(Thread::start);
        threads.forEach(t -> { try { t.join(); } catch (InterruptedException ignored) {} });

        check(processedBy.size() == N, "every message processed exactly once");
        System.out.println("Part 4 OK (" + N + " messages × " + CONSUMERS + " consumers, no duplicates)");
    }

    /* ----- assertion helpers ----- */

    private static void check(boolean cond) { check(cond, "assertion failed"); }
    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }
}
