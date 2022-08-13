package google.vo;

import java.util.*;

public class FindAllPossibleRecipesfromGivenSupplies {
    public static List<String> findAllRecipes(String[] recipes, List<List<String>> ingredients, String[] supplies) {
        List<String> result = new ArrayList<>();
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> map = new HashMap<>();

        for (int i = 0; i < recipes.length; i++) {
            String recipe = recipes[i];
            List<String> ingredient = ingredients.get(i);
            indegree.put(recipe, ingredient.size());
            for (String in : ingredient) {
                map.putIfAbsent(in, new ArrayList<>());
                map.get(in).add(recipe);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (String recipe : supplies){
            queue.add(recipe);
        }

        while (!queue.isEmpty()) {
            String cur = queue.poll();
            if (map.containsKey(cur)) {
                for (String next : map.get(cur)) {
                    int val = indegree.get(next) - 1;
                    if (val == 0) {
                        result.add(next);
                        queue.add(next);
                    }
                    indegree.put(next, val);

                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String[] recipes = {"bread","sandwich"}, supplies = {"yeast","flour","meat"};
        List<List<String>> ingredients = Arrays.asList(
                Arrays.asList("yeast","flour"),
                Arrays.asList("bread","meat")
        );

        System.out.println(findAllRecipes(recipes, ingredients, supplies));
    }
}
