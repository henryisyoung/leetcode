package snowflake.mianjing;

/*
Debugging Service Failures
Problem Context
You need to investigate a system crash by looking at logs and how services connect to each other.

The interview is divided into three parts:

Find the first error log using Binary Search.
Find every service that will break using BFS or DFS.
Find the longest chain of broken services using DFS.
Part 1: Finding the First Error (Binary Search)
The Challenge
You have a list of strings called logs. Each line begins with one of these tags:

[Info]
[Warn]
[Error]
You must follow these rules:

Once an [Error] appears, every log line after it is also an [Error].
If there is an error at a specific line, the line right before it must be a [Warn].
Your task is to find the index of the very first [Error]. If there are no errors, return -1.

Example Case
logs = [
    "[Info] boot",
    "[Info] warmup",
    "[Warn] timeout retries high",
    "[Error] downstream unavailable",
    "[Error] service unhealthy",
]
# answer = 3
Approach and Solution
The logs follow a strict order: everything before the first error is "safe," and everything from the first error onwards is "error." Because the data is sorted this way, we can use Binary Search to find the exact point where it changes.

Part 2: Finding All Affected Services (Graph BFS/DFS)
The Challenge
Now you receive a list showing which service calls which:

calls = {
    "A": ["B", "C"],  # A calls B and C
    "B": ["D"],
    "C": ["D"],
    "E": ["A"],
    "F": ["C"],
}
The rule is: If a service crashes, any service that relies on it (directly or indirectly) will also fail.

You are given the name of the first_error_service. You need to return a set of all services that will eventually fail. The order of the output does not matter.

Example Case
first_error_service = "D"
# impacted = {"D", "B", "C", "A", "E", "F"}
Approach and Solution
We need to see who depends on the broken service. We can do this by walking backward through the connections:

Build a reverse graph. This maps a service to the ones that call it (callee -> callers).
Start a Breadth-First Search (BFS) beginning at the first_error_service.
Visit every service that relies on the current one.

Part 3: Finding the Longest Chain of Errors (DFS)
The Challenge
Building on Part 2, you now need to find one longest path of errors starting from the first_error_service.

For example, if service D fails, a chain of failures might look like this:

D -> B -> A -> E
This means D fails first, causing B to fail, which causes A to fail, and finally E.

Approach and Solution
We can use Depth-First Search (DFS) on the same reverse graph we built before.

Since the graph is a Directed Acyclic Graph (DAG), we can make this faster using memoization:

dfs(x) calculates the longest chain starting at service x.
For each service, we look at its callers and pick the one that results in the longest chain.
 */


import java.util.*;

public class ServiceFailureForensics {
    public int findFirstErr(String[] logs) {
        if (logs == null || logs.length == 0) {
            return -1;
        }
        int n = logs.length;
        int left = 0, right = n - 1;
        while (left + 1 < right) {
            int mid = left + (right - left) / 2;
            if (logs[mid].startsWith("[Error]")) {
                right = mid;
            } else {
                left = mid;
            }
        }
        if (logs[left].startsWith("[Error]")) return left;
        if (logs[right].startsWith("[Error]")) return right;
        return -1;
    }

    public List<String> impactedServices(String service, Map<String, List<String>> dependencies) {
        List<String> result = new ArrayList<>();

        Map<String, List<String>> graph = new HashMap<>();
        for (Map.Entry<String, List<String>> e : dependencies.entrySet()) {
            String caller = e.getKey();
            for (String callee : e.getValue()) {
                graph.computeIfAbsent(callee, k -> new ArrayList<>()).add(caller);
            }
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(service);
        visited.add(service);

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            result.add(cur);
            List<String> callers = graph.get(cur);
            if (callers == null) continue;
            for (String next : callers) {
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return result;
    }

    // Part 3: longest chain of failures starting from `service`, walked over the
    // reverse graph (callee -> callers). Memoized DFS on a DAG.
    public List<String> longestChain(String service, Map<String, List<String>> dependencies) {
        Map<String, List<String>> graph = new HashMap<>();
        for (Map.Entry<String, List<String>> e : dependencies.entrySet()) {
            String caller = e.getKey();
            for (String callee : e.getValue()) {
                graph.computeIfAbsent(callee, k -> new ArrayList<>()).add(caller);
            }
        }
        Map<String, List<String>> memo = new HashMap<>();
        return dfsLongest(service, graph, memo);
    }

    private List<String> dfsLongest(String node,
                                    Map<String, List<String>> graph,
                                    Map<String, List<String>> memo) {
        List<String> cached = memo.get(node);
        if (cached != null) return cached;

        List<String> bestSuffix = Collections.emptyList();
        List<String> callers = graph.get(node);
        if (callers != null) {
            for (String next : callers) {
                List<String> sub = dfsLongest(next, graph, memo);
                if (sub.size() > bestSuffix.size()) {
                    bestSuffix = sub;
                }
            }
        }

        List<String> path = new ArrayList<>(bestSuffix.size() + 1);
        path.add(node);
        path.addAll(bestSuffix);
        memo.put(node, path);
        return path;
    }
}