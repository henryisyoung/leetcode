package snowflake;

import java.util.*;

public class ThroneInheritance {

    private final String king;
    // parent name -> ordered list of child names (insertion order = birth order)
    private final Map<String, List<String>> children = new HashMap<>();
    private final Set<String> dead = new HashSet<>();

    public ThroneInheritance(String kingName) {
        this.king = kingName;
        children.put(kingName, new ArrayList<>());
    }

    public void birth(String parentName, String childName) {
        children.computeIfAbsent(parentName, k -> new ArrayList<>()).add(childName);
        children.putIfAbsent(childName, new ArrayList<>());
    }

    public void death(String name) {
        dead.add(name);
    }

    // Iterative preorder DFS:
    //   - O(n) total (no per-node list copies)
    //   - no recursion, so no stack-overflow risk on deep trees
    //   - result list is pre-sized to its known upper bound
    public List<String> getInheritanceOrder() {
        List<String> result = new ArrayList<>(children.size() - dead.size());
        Deque<String> stack = new ArrayDeque<>();
        stack.push(king);

        while (!stack.isEmpty()) {
            String cur = stack.pop();
            if (!dead.contains(cur)) {
                result.add(cur);
            }
            // Push children in reverse so the leftmost (eldest) is processed first.
            List<String> kids = children.get(cur);
            for (int i = kids.size() - 1; i >= 0; i--) {
                stack.push(kids.get(i));
            }
        }
        return result;
    }
}
