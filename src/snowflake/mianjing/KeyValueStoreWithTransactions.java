package snowflake.mianjing;

import java.util.HashMap;
import java.util.Map;

/*
Problem Summary
Design and build an in-memory key-value store. This interview question has 3 parts:

Create basic get, set, and delete functions.
Add transaction features: begin, commit, and rollback.
Make the code work safely with multiple threads (Multithreading).
This tests your knowledge of data structures, how to handle data updates, and how to manage concurrency.

Usage Example
db = KeyValueStore()
db.set("a", 1)
assert db.get("a") == 1

db.begin()
db.set("a", 2)
assert db.get("a") == 2
db.rollback()
assert db.get("a") == 1

db.begin()
db.set("b", 3)
db.commit()
assert db.get("b") == 3
Part 1: Basic Operations
The Task
Build a simple key-value store using a class:

class KeyValueStore:
    def get(self, key: str):
        pass

    def set(self, key: str, value):
        pass

    def delete(self, key: str):
        pass
What it Must Do
set(key, value): Save a new key or update an existing one.
get(key): Return the value. If the key does not exist, return None.
delete(key): Remove the key if it exists.
Example Tests
db = KeyValueStore()
assert db.get("missing") is None

db.set("x", 10)
assert db.get("x") == 10

db.set("x", 20)
assert db.get("x") == 20

db.delete("x")
assert db.get("x") is None

Part 2: Adding Transactions
The Task
Update the store so it can handle a single transaction:

class KeyValueStore:
    def begin(self):
        pass

    def commit(self):
        pass

    def rollback(self):
        pass
What it Must Do
begin(): Start a new transaction.
commit(): Save all temporary changes to the main store.
rollback(): Throw away all temporary changes.
If you read (get) during a transaction, you must see your temporary changes.
If you call commit() or rollback() without starting a transaction, raise an error.
Example Tests
db = KeyValueStore()
db.set("a", 1)
assert db.get("a") == 1

db.begin()
db.set("a", 2)
db.set("b", 3)
assert db.get("a") == 2
assert db.get("b") == 3
db.rollback()
assert db.get("a") == 1
assert db.get("b") is None

db.begin()
db.delete("a")
assert db.get("a") is None
db.commit()
assert db.get("a") is None
Solution for Part 2
We use a temporary dictionary called txn. It holds keys that we change during the transaction. This keeps begin and rollback fast (O(1)). commit takes O(k) time, where k is the number of keys we changed.
Solution for Part 2
We use a temporary dictionary called txn. It holds keys that we change during the transaction. This keeps begin and rollback fast (O(1)). commit takes O(k) time, where k is the number of keys we changed.

class _Deleted:
    pass

_DELETED = _Deleted()

class KeyValueStore:
    def __init__(self):
        self._store = {}
        self._txn = None  # Stores changed keys: key -> value

    def get(self, key: str):
        # Check transaction first
        if self._txn is not None and key in self._txn:
            value = self._txn[key]
            return None if value is _DELETED else value
        # Fallback to main store
        return self._store.get(key)

    def set(self, key: str, value):
        if self._txn is not None:
            self._txn[key] = value
        else:
            self._store[key] = value

    def delete(self, key: str):
        if self._txn is not None:
            self._txn[key] = _DELETED
        else:
            self._store.pop(key, None)

    def begin(self):
        if self._txn is not None:
            raise RuntimeError("transaction already active")
        self._txn = {}

    def commit(self):
        if self._txn is None:
            raise RuntimeError("no active transaction")

        # Move changes to main store
        for key, value in self._txn.items():
            if value is _DELETED:
                self._store.pop(key, None)
            else:
                self._store[key] = value
        self._txn = None

    def rollback(self):
        if self._txn is None:
            raise RuntimeError("no active transaction")
        self._txn = None

Part 3: Handling Multiple Threads
The Task
Now, allow multiple threads to use the store at the same time. Each thread needs its own safe transaction.

What it Must Do
Operations must be thread-safe (no crashes or corruption).
commit() and rollback() must happen all at once (atomic).
Each thread must have its own private transaction data.
Different threads should not mess up each other's work.
How to Solve It
We will use thread-local storage and optimistic locking:

Thread-local: Each thread keeps its own temporary changes using threading.local().
Locking: We use a lock to protect the main global store.
Versions: We track a version number for every key in the global store.
Optimistic Commit: When a thread tries to commit, we check the version numbers. If a key was changed by someone else since we first read it, we stop and raise an error.
This gives us:

O(1) speed for begin, rollback, and operations inside a transaction.
Safe updates using the lock.
Solution for Part 3
import threading

class _Deleted:
    pass

_DELETED = _Deleted()

class KeyValueStore:
    def __init__(self):
        self._store = {}
        self._versions = {}  # key -> integer version
        self._lock = threading.RLock()
        self._local = threading.local()

    def _get_txn(self):
        # Get transaction for the current thread
        return getattr(self._local, "txn", None)

    def _set_txn(self, txn):
        # Set transaction for the current thread
        self._local.txn = txn

    def begin(self):
        if self._get_txn() is not None:
            raise RuntimeError("transaction already active in this thread")
        self._set_txn({
            "writes": {},            # key -> new value
            "seen_versions": {},     # key -> version at start
        })

    def get(self, key: str):
        txn = self._get_txn()
        # Check local transaction first
        if txn is not None and key in txn["writes"]:
            value = txn["writes"][key]
            return None if value is _DELETED else value

        # Read from global store (safe with lock)
        with self._lock:
            return self._store.get(key)

    def set(self, key: str, value):
        txn = self._get_txn()
        if txn is None:
            # No transaction: write directly
            with self._lock:
                self._store[key] = value
                self._versions[key] = self._versions.get(key, 0) + 1
            return

        # Record version if this is the first time we see this key
        if key not in txn["seen_versions"]:
            with self._lock:
                txn["seen_versions"][key] = self._versions.get(key, 0)
        txn["writes"][key] = value

    def delete(self, key: str):
        txn = self._get_txn()
        if txn is None:
            # No transaction: delete directly
            with self._lock:
                if key in self._store:
                    self._store.pop(key, None)
                    self._versions[key] = self._versions.get(key, 0) + 1
            return

        if key not in txn["seen_versions"]:
            with self._lock:
                txn["seen_versions"][key] = self._versions.get(key, 0)
        txn["writes"][key] = _DELETED

    def commit(self):
        txn = self._get_txn()
        if txn is None:
            raise RuntimeError("no active transaction")

        with self._lock:
            # Check for conflicts
            for key, seen_version in txn["seen_versions"].items():
                if self._versions.get(key, 0) != seen_version:
                    raise RuntimeError(f"write conflict on key '{key}'")

            # Apply changes
            for key, value in txn["writes"].items():
                if value is _DELETED:
                    self._store.pop(key, None)
                else:
                    self._store[key] = value
                self._versions[key] = self._versions.get(key, 0) + 1

        self._set_txn(None)

    def rollback(self):
        if self._get_txn() is None:
            raise RuntimeError("no active transaction")
        self._set_txn(None)
Time Complexity
| Operation | Time | Notes | | --- | --- | --- | | begin() | O(1) | setup local object | | get() | O(1) average | local lookup + global lookup | | set() | O(1) average | save to local map | | delete() | O(1) average | save to local map | | rollback() | O(1) | clear local map | | commit() | O(k) | validate + apply k changes |

Pros and Cons
Simple: It is easier to write than using a lock for every single key.
Safe: Commits use one lock, so data never gets corrupted.
Conflict Detection: By checking versions, we stop threads from accidentally overwriting new data from other threads.
Tradeoff: If many threads write at the exact same time, some might fail (conflict error) and need to try again.
 */
/**
 * Part 1 + Part 2: in-memory KV store with a SINGLE active transaction.
 *
 *   - get/set/delete are O(1) average.
 *   - begin/rollback are O(1). commit is O(k) where k = # keys touched in txn.
 *   - During a transaction, reads see local writes ("read-your-writes").
 *   - commit/rollback without an active transaction → IllegalStateException.
 *   - Nested begin (already in a txn) → IllegalStateException.
 *
 * Strategy: a separate `txn` map holds pending changes. A sentinel `DELETED`
 * marker distinguishes "delete this key on commit" from "no entry in txn".
 */
public class KeyValueStoreWithTransactions {

    /** Sentinel meaning "this key is deleted in the current transaction". */
    private static final Object DELETED = new Object();

    private final Map<String, Integer> store = new HashMap<>();
    private Map<String, Object> txn = null;   // null = no active transaction

    public Integer get(String key) {
        if (txn != null && txn.containsKey(key)) {
            Object v = txn.get(key);
            return v == DELETED ? null : (Integer) v;
        }
        return store.get(key);
    }

    public void set(String key, int value) {
        if (txn != null) {
            txn.put(key, value);
        } else {
            store.put(key, value);
        }
    }

    public void delete(String key) {
        if (txn != null) {
            // Record "delete on commit" — even if the key isn't in main store yet,
            // this masks any prior set in the same txn.
            txn.put(key, DELETED);
        } else {
            store.remove(key);
        }
    }

    public void begin() {
        if (txn != null) {
            throw new IllegalStateException("transaction already active");
        }
        txn = new HashMap<>();
    }

    public void commit() {
        if (txn == null) {
            throw new IllegalStateException("no active transaction");
        }
        for (Map.Entry<String, Object> e : txn.entrySet()) {
            if (e.getValue() == DELETED) {
                store.remove(e.getKey());
            } else {
                store.put(e.getKey(), (Integer) e.getValue());
            }
        }
        txn = null;
    }

    public void rollback() {
        if (txn == null) {
            throw new IllegalStateException("no active transaction");
        }
        txn = null;
    }

    // ============================================================
    // Part 3: Multi-threaded variant
    // ============================================================
    //
    // Each thread has its own transaction (ThreadLocal). The main store is
    // protected by a single lock. We use OPTIMISTIC concurrency control:
    //   - Every key carries an integer version number.
    //   - When a txn first touches a key, we record the version it observed.
    //   - At commit time, under the lock, we verify those versions still
    //     match. If they don't, another thread changed the key — conflict.
    //   - On conflict the txn is aborted and the caller can retry.
    //
    // Rationale (interview pitch):
    //   - Reads and writes inside a txn never block other threads.
    //   - Only commit takes the lock, briefly.
    //   - Two threads writing the same key see a deterministic winner
    //     (whoever commits first) instead of corrupted state.

    public static class Concurrent {
        private static final Object DELETED = new Object();

        private final Map<String, Integer> store = new HashMap<>();
        private final Map<String, Long>    versions = new HashMap<>();
        private final Object lock = new Object();

        private static class Txn {
            final Map<String, Object> writes = new HashMap<>(); // pending writes / deletes
            final Map<String, Long>   seen   = new HashMap<>(); // key -> version observed
        }
        private final ThreadLocal<Txn> local = new ThreadLocal<>();

        public void begin() {
            if (local.get() != null) {
                throw new IllegalStateException("transaction already active in this thread");
            }
            local.set(new Txn());
        }

        public Integer get(String key) {
            Txn t = local.get();
            if (t != null && t.writes.containsKey(key)) {
                Object v = t.writes.get(key);
                return v == DELETED ? null : (Integer) v;
            }
            synchronized (lock) {
                if (t != null) {
                    // Snapshot the version we read at — used for commit-time conflict check.
                    t.seen.putIfAbsent(key, versions.getOrDefault(key, 0L));
                }
                return store.get(key);
            }
        }

        public void set(String key, int value) {
            Txn t = local.get();
            if (t == null) {
                // No txn → write immediately under the lock and bump version.
                synchronized (lock) {
                    store.put(key, value);
                    versions.merge(key, 1L, Long::sum);
                }
                return;
            }
            if (!t.seen.containsKey(key)) {
                synchronized (lock) {
                    t.seen.put(key, versions.getOrDefault(key, 0L));
                }
            }
            t.writes.put(key, value);
        }

        public void delete(String key) {
            Txn t = local.get();
            if (t == null) {
                synchronized (lock) {
                    if (store.remove(key) != null) {
                        versions.merge(key, 1L, Long::sum);
                    }
                }
                return;
            }
            if (!t.seen.containsKey(key)) {
                synchronized (lock) {
                    t.seen.put(key, versions.getOrDefault(key, 0L));
                }
            }
            t.writes.put(key, DELETED);
        }

        public void commit() {
            Txn t = local.get();
            if (t == null) {
                throw new IllegalStateException("no active transaction");
            }
            try {
                synchronized (lock) {
                    // 1. Validate: every key we touched must still be at the version we saw.
                    for (Map.Entry<String, Long> e : t.seen.entrySet()) {
                        long current = versions.getOrDefault(e.getKey(), 0L);
                        if (current != e.getValue()) {
                            throw new IllegalStateException(
                                    "write conflict on key '" + e.getKey() + "'");
                        }
                    }
                    // 2. Apply: writes go to store, version is bumped per touched key.
                    for (Map.Entry<String, Object> e : t.writes.entrySet()) {
                        if (e.getValue() == DELETED) {
                            store.remove(e.getKey());
                        } else {
                            store.put(e.getKey(), (Integer) e.getValue());
                        }
                        versions.merge(e.getKey(), 1L, Long::sum);
                    }
                }
            } finally {
                // Whether commit succeeded or threw a conflict, the txn is over.
                local.remove();
            }
        }

        public void rollback() {
            if (local.get() == null) {
                throw new IllegalStateException("no active transaction");
            }
            local.remove();
        }
    }

    // ============================================================
    // Demo / tests
    // ============================================================
    public static void main(String[] args) throws Exception {
        // ---- Part 2 ----
        KeyValueStoreWithTransactions db = new KeyValueStoreWithTransactions();
        db.set("a", 1);
        check(db.get("a") == 1, "get a == 1");

        db.begin();
        db.set("a", 2);
        db.set("b", 3);
        check(db.get("a") == 2, "txn read-your-writes a==2");
        check(db.get("b") == 3, "txn read-your-writes b==3");
        db.rollback();
        check(db.get("a") == 1, "after rollback a==1");
        check(db.get("b") == null, "after rollback b==null");

        db.begin();
        db.delete("a");
        check(db.get("a") == null, "txn delete masks a");
        db.commit();
        check(db.get("a") == null, "after commit a==null");

        // commit without begin → error
        boolean thrown = false;
        try { db.commit(); } catch (IllegalStateException e) { thrown = true; }
        check(thrown, "commit without begin throws");

        // nested begin → error
        db.begin();
        thrown = false;
        try { db.begin(); } catch (IllegalStateException e) { thrown = true; }
        check(thrown, "nested begin throws");
        db.rollback();

        System.out.println("Part 2 OK");

        // ---- Part 3 ----
        Concurrent cdb = new Concurrent();
        cdb.set("x", 100);
        check(cdb.get("x") == 100, "concurrent: direct set/get");

        // Two threads racing on same key — one wins, the other sees a conflict.
        Thread t1 = new Thread(() -> {
            cdb.begin();
            cdb.set("x", 1);
            sleep(50);          // give t2 time to commit first
            try { cdb.commit(); }
            catch (IllegalStateException ex) {
                // expected: conflict
                System.out.println("t1 conflict: " + ex.getMessage());
            }
        });
        Thread t2 = new Thread(() -> {
            sleep(10);
            cdb.begin();
            cdb.set("x", 2);
            cdb.commit();
        });
        t1.start(); t2.start();
        t1.join();  t2.join();
        check(cdb.get("x") == 2, "after race: t2 wins, x==2");

        // Different keys → both commits succeed.
        Thread ta = new Thread(() -> {
            cdb.begin();
            cdb.set("k1", 11);
            cdb.commit();
        });
        Thread tb = new Thread(() -> {
            cdb.begin();
            cdb.set("k2", 22);
            cdb.commit();
        });
        ta.start(); tb.start();
        ta.join();  tb.join();
        check(cdb.get("k1") == 11, "disjoint keys: k1==11");
        check(cdb.get("k2") == 22, "disjoint keys: k2==22");

        System.out.println("Part 3 OK");
    }

    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError("FAILED: " + msg);
    }
    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
