package snowflake.mianjing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
Problem Description
You have a main list of ingredients called ingredients. You also have a list of recipes. Each recipe is just a smaller list of ingredients.

For every recipe, you need to check if it exists inside the main ingredients list. The recipe must appear as a solid block (contiguous sequence). You cannot skip ingredients or change their order.

The interview usually has three parts:

Basic Solution: Use preprocessing to make searches fast.
Follow-up 1: Save memory (use only O(1) extra space).
Follow-up 2: Handle a massive stream of data.
Example Input
ingredients = ["bun", "lettuce", "tomato", "patty", "cheese", "onion"]
recipes = [
    ["lettuce", "tomato", "patty"],  # True (It is right there in the middle)
    ["tomato", "cheese"],            # False ("patty" is between them)
    ["patty", "cheese"],             # True (They are next to each other)
]
Part 1: Basic Solution (Hash Approach)
Task
Write a function match_recipes that takes the full list and the recipes. It should return a list of True or False values.

Rules
You must keep the exact order of ingredients.
The match must be contiguous (no gaps).
The solution should handle many recipe queries quickly.

Part 2: Low Memory Constraint
New Requirement
Now, assume you are very low on memory. You cannot use O(n^2) space for HashMaps. You must solve the problem using only O(1) extra space.

How to Solve
We can use a simple "Two-Pointer" or sliding window method.

Take a recipe.
Try to match it starting at index 0 of the ingredient list.
If it doesn't match, try starting at index 1, then index 2, and so on.
Do this for every recipe.

Part 3: Streaming Data
The Challenge
Now imagine the ingredient list is too big to store in memory. The ingredients arrive one by one as a "stream". You still need to identify which recipes appear.

Strategy
We use the Aho-Corasick algorithm. This builds a special search tree (Trie).

Build a Trie: Put all recipes into a prefix tree.
Fail Links: Add special links that tell us where to jump if a partial match fails.
Process Stream: Read ingredients one by one. Move through the tree. If we reach the end of a recipe in the tree, we mark it as found
 */
public class RecipeSequenceMatcher {
    private static final String SEP = "\u0001";

    // Part 1: hash every contiguous window of ingredients up to the longest
    // recipe length, then answer each recipe in O(n) with a single set lookup.
    public List<Boolean> containsRecipeHash(List<String> ingredients, List<List<String>> recipes) {
        List<Boolean> result = new ArrayList<>();
        if (recipes.isEmpty()) return result;

        int maxLen = 0;
        for (List<String> r : recipes) {
            maxLen = Math.max(maxLen, r.size());
        }
        if (maxLen == 0) {
            for (List<String> r : recipes) result.add(true);
            return result;
        }

        Set<String> windows = new HashSet<>();
        int m = ingredients.size();
        for (int start = 0; start < m; start++) {
            StringBuilder sb = new StringBuilder();
            int end = Math.min(m, start + maxLen);
            for (int i = start; i < end; i++) {
                if (i > start) sb.append(SEP);
                sb.append(ingredients.get(i));
                windows.add(sb.toString());
            }
        }

        for (List<String> recipe : recipes) {
            if (recipe.isEmpty()) {
                result.add(true);
                continue;
            }
            result.add(windows.contains(join(recipe)));
        }
        return result;
    }

    private String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(SEP);
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    // Part 2: O(1) extra space sliding window.
    public List<Boolean> containsRecipe(List<String> ingredients, List<List<String>> recipes) {
        List<Boolean> result = new ArrayList<>();
        for (List<String> recipe : recipes) {
            result.add(findMatch(recipe, ingredients));
        }
        return result;
    }

    private Boolean findMatch(List<String> recipe, List<String> ingredients) {
        int n = recipe.size(), m = ingredients.size();
        if (n == 0) return true;
        if (n > m) return false;

        for (int start = 0; start <= m - n; start++) {
            int count = 0;
            while (count < n && recipe.get(count).equals(ingredients.get(start + count))) {
                count++;
            }
            if (count == n) {
                return true;
            }
        }
        return false;
    }

    // Part 3: streaming Aho-Corasick over recipes (each "symbol" is an ingredient).
    // Reads ingredients one at a time, never stores the full stream.
    public List<Boolean> containsRecipeStream(Iterable<String> ingredientsStream,
                                              List<List<String>> recipes) {
        AhoCorasick ac = new AhoCorasick(recipes);
        boolean[] found = new boolean[recipes.size()];
        Arrays.fill(found, false);

        AcNode node = ac.root;
        for (int id : node.outputs) {
            found[id] = true;
        }
        Iterator<String> it = ingredientsStream.iterator();
        while (it.hasNext()) {
            String word = it.next();
            node = ac.next(node, word);
            for (int id : node.outputs) {
                found[id] = true;
            }
        }

        List<Boolean> result = new ArrayList<>(recipes.size());
        for (boolean b : found) result.add(b);
        return result;
    }

    private static class AcNode {
        Map<String, AcNode> children = new HashMap<>();
        AcNode fail;
        // recipe ids that end at this node (from this node or any fail-link ancestor)
        List<Integer> outputs = new ArrayList<>();
    }

    private static class AhoCorasick {
        final AcNode root = new AcNode();

        AhoCorasick(List<List<String>> recipes) {
            // 1) Build the trie. Empty recipes match immediately at the root.
            for (int id = 0; id < recipes.size(); id++) {
                List<String> recipe = recipes.get(id);
                AcNode cur = root;
                for (String w : recipe) {
                    cur = cur.children.computeIfAbsent(w, k -> new AcNode());
                }
                cur.outputs.add(id);
            }
            // 2) BFS to compute fail links and merge outputs along them.
            Deque<AcNode> queue = new ArrayDeque<>();
            root.fail = root;
            for (Map.Entry<String, AcNode> e : root.children.entrySet()) {
                e.getValue().fail = root;
                queue.offer(e.getValue());
            }
            while (!queue.isEmpty()) {
                AcNode u = queue.poll();
                for (Map.Entry<String, AcNode> e : u.children.entrySet()) {
                    String w = e.getKey();
                    AcNode v = e.getValue();
                    AcNode f = u.fail;
                    while (f != root && !f.children.containsKey(w)) {
                        f = f.fail;
                    }
                    AcNode candidate = f.children.get(w);
                    v.fail = (candidate != null && candidate != v) ? candidate : root;
                    v.outputs.addAll(v.fail.outputs);
                    queue.offer(v);
                }
            }
        }

        // Single transition: walk fail links until we find a child for `word`,
        // or fall back to root.
        AcNode next(AcNode node, String word) {
            while (node != root && !node.children.containsKey(word)) {
                node = node.fail;
            }
            AcNode child = node.children.get(word);
            return child != null ? child : root;
        }
    }

}
