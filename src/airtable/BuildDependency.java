package airtable;

import java.util.*;

public class BuildDependency {
    /*
    {
        {"foo.h", {}},
        {"bar.h", {}},
        {"foo.o", {"foo.h"}},
        {"bar.o", {"bar.h"}},
        {"bin", {"foo.o", "bar.o"}}
    }
    1. List<String> init(target) 返回如果要完成这个target，有哪些tasks现在可以开始做。（也就是没有任何dependent）
    2. List<String> onComplete(task) 返回如果完成task，有哪些tasks被unblock。（也就是完成当前task，哪些被unblock）
    follow up，如果有些和target不想关的task怎么办。（hashset，target只存相关的）
     */

    public static List<String> init(String target, List<List<String>> dependencies) {
        Set<String> visited = new HashSet<>();
        Map<String, List<String>> map = new HashMap<>();
        for (List<String> pairs : dependencies) {
            String a = pairs.get(0);
            List<String> dependency = pairs.subList(1, pairs.size());
            map.put(a, dependency);
        }
        List<String> result = new ArrayList<>();
        System.out.println(map);
        dfsFind(result, visited, map, target);
        return result;
    }

    private static void dfsFind(List<String> result, Set<String> visited, Map<String, List<String>> map, String cur) {
        if (map.containsKey(cur) && map.get(cur).isEmpty()) {
            result.add(cur);
            return;
        }
        visited.add(cur);
        for (String next : map.get(cur)) {
            if (visited.contains(next)) {
                continue;
            }
            dfsFind(result, visited, map, next);
        }
    }

    public static void main(String[] args) {
        List<List<String>> lists = Arrays.asList(
                Arrays.asList("foo.h"),
                Arrays.asList("bar.h"),
                Arrays.asList("foo.o","foo.h"),
                Arrays.asList("bar.o", "bar.h"),
                Arrays.asList("bin", "foo.o", "bar.o")
        );
        Map<String, List<String>> map = new HashMap<>();

        System.out.println(init("bin", lists));
    }
}
