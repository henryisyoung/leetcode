package snowflake.mianjing;
/*
Problem Summary
Imagine a tree structure where every node is a separate server.

Each node can only talk to its parent and its children.
Communication is asynchronous (it happens in the background).
Messages might arrive late or in the wrong order.
Your goal is to write code for the nodes so they can count the total number of nodes in the entire tree.

The interview usually has 3 parts:

Basic Counting: Counting assuming messages work perfectly.
Duplicate Handling: Handling cases where a message arrives twice.
Reliability: Handling cases where messages get lost (packet loss).
Provided Code Interface
You must use these classes. You cannot change the send_async function; consider it a black box that handles the network.

class Message:
    def __init__(self, kind: str, request_id: str, payload: dict):
        self.kind = kind
        self.request_id = request_id
        self.payload = payload

class Node:
    def __init__(self, node_id: str, parent_id: str | None, children: list[str]):
        self.node_id = node_id
        self.parent_id = parent_id
        self.children = children

    def send_async(self, to_node_id: str, message: Message) -> None:
        """Network API (already provided). usage: send_async(id, msg)"""
        pass

    def call(self, from_node_id: str | None, message: Message) -> None:
        """You need to implement this logic."""
        pass
Part 1: Basic Counting Logic
Task Goal
Write the call function to handle:

request_count: A parent asks a child to count its subtree.
reply_count: A child sends its result back to the parent.
Only the Root node should output the final total number.

Rules
Leaf Nodes: Immediately reply with 1.
Internal Nodes:
Send request_count to all children.
Wait for all replies.
Calculate 1 + sum(child_counts).
Send the result to the parent.
Root Node: Waits for all replies, then prints the final number.
Concurrency: Use a Lock to protect data because messages arrive at the same time.
Example
        1
      / | \
     2  3  4
       / \
      5   6
The root (1) should output 6.

Part 2: Handling Duplicate Messages
Interview Challenge
Sometimes the network is glitchy. The send_async function might try sending the same message multiple times (retries). This means a node might receive duplicate request_count or reply_count messages.

How do you prevent counting the same node twice?

Strategy
We need to make our operations idempotent (safe to repeat).

Track Requests: If we already finished a request, save the answer.
Duplicate Request: If a parent asks again, send the saved answer immediately.
Duplicate Reply: If a child replies again, ignore it.

 */


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedTreeNodeCount {

    static class Message {
        String kind;                     // "request_count" | "reply_count"
        String requestId;
        Map<String, Object> payload;

        Message(String kind, String requestId, Map<String, Object> payload) {
            this.kind = kind;
            this.requestId = requestId;
            this.payload = payload;
        }
    }

    static class Node {
        String nodeId;
        String parentId;
        List<String> children;

        // Per-request state — keyed by requestId so concurrent counts don't mix.
        Map<String, String>  replyTo     = new HashMap<>();   // who asked us
        Map<String, Integer> outstanding = new HashMap<>();   // child replies still owed
        Map<String, Integer> childSum    = new HashMap<>();   // sum collected so far

        Node(String nodeId, String parentId, List<String> children) {
            this.nodeId = nodeId;
            this.parentId = parentId;
            this.children = children;
        }

        // Provided by the framework; treat as a black box.
        void sendAsync(String toNodeId, Message msg) {
            Network.deliver(this.nodeId, toNodeId, msg);
        }

        // The interview-implementable part. `synchronized` is the simplest
        // correct lock: messages arriving on different threads serialise
        // through this method, no race on the Maps above.
        synchronized void call(String from, Message msg) {
            String reqId = msg.requestId;

            if (msg.kind.equals("request_count")) {
                // Leaf: I am 1 node. Reply immediately.
                if (children.isEmpty()) {
                    sendAsync(from, replyMsg(reqId, 1));
                    return;
                }
                // Internal: remember who asked, fan out to children.
                replyTo.put(reqId, from);
                outstanding.put(reqId, children.size());
                childSum.put(reqId, 0);
                for (String c : children) {
                    sendAsync(c, new Message("request_count", reqId, new HashMap<>()));
                }
                return;
            }

            if (msg.kind.equals("reply_count")) {
                int count = (int) msg.payload.get("count");
                childSum.merge(reqId, count, Integer::sum);
                int left = outstanding.merge(reqId, -1, Integer::sum);
                if (left > 0) return;          // still waiting on other children

                int total = 1 + childSum.get(reqId);
                String parent = replyTo.get(reqId);
                replyTo.remove(reqId);
                outstanding.remove(reqId);
                childSum.remove(reqId);

                if (parent == null) {
                    System.out.println("total = " + total);   // root prints
                } else {
                    sendAsync(parent, replyMsg(reqId, total));
                }
            }
        }

        private static Message replyMsg(String reqId, int count) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("count", count);
            return new Message("reply_count", reqId, payload);
        }
    }

    /* -------------- tiny test harness (not part of the interview answer) -- */

    static class Network {
        static Map<String, Node> nodes = new HashMap<>();
        static void register(Node n) { nodes.put(n.nodeId, n); }
        static void deliver(String from, String to, Message msg) {
            // Synchronous delivery is fine for the demo — `call` is synchronized
            // so the locking story still holds in real async delivery.
            Node target = nodes.get(to);
            if (target != null) target.call(from, msg);
        }
    }

    public static void main(String[] args) {
        /*
         *         1
         *       / | \
         *      2  3  4
         *        / \
         *       5   6
         * Expected: total = 6
         */
        Network.register(new Node("1", null, List.of("2", "3", "4")));
        Network.register(new Node("2", "1",  List.of()));
        Network.register(new Node("3", "1",  List.of("5", "6")));
        Network.register(new Node("4", "1",  List.of()));
        Network.register(new Node("5", "3",  List.of()));
        Network.register(new Node("6", "3",  List.of()));

        // Kick off: deliver a request_count to the root with `from = null`.
        Network.nodes.get("1").call(null, new Message("request_count", "req-1", new HashMap<>()));
    }
}
