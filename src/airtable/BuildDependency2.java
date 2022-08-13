package airtable;

import java.util.*;

public class BuildDependency2 {
    /*
    {
        {"foo.h", {}},
        {"bar.h", {}},
        {"bar.t", {}},
        {"foo.o", {"foo.h"}},
        {"bar.o", {"bar.h"}},
        {"bin", {"foo.o", "bar.o"}}
    }
    1. List<String> init(target) 返回如果要完成这个target，有哪些tasks现在可以开始做。（也就是没有任何dependent）
    2. List<String> onComplete(task) 返回如果完成task，有哪些tasks被unblock。（也就是完成当前task，哪些被unblock）
    follow up，如果有些和target不想关的task怎么办。（hashset，target只存相关的）
     */

    public static List<String> init(String target, Map<String, List<String>> dependencies) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        dfsFindAllLeaf(target, dependencies, result, visited);
        return result;
    }

    private static void dfsFindAllLeaf(String cur, Map<String, List<String>> dependencies,
                                       List<String> result, Set<String> visited) {
        visited.add(cur);
        if (dependencies.containsKey(cur) && dependencies.get(cur).isEmpty()) {
            result.add(cur);
            return;
        }
        if (dependencies.containsKey(cur)) {
            for (String next : dependencies.get(cur)) {
                if (visited.contains(next)) {
                    continue;
                }
                dfsFindAllLeaf(next, dependencies, result, visited);
            }
        }
    }

    public static List<String> onComplete(String task, Map<String, List<String>> dependencies) {
        List<String> result = new ArrayList<>();
        return result;
    }

    public static void main(String[] args) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("foo.h", Arrays.asList());
        map.put("bar.h", Arrays.asList("foo.h", "foo.o"));
        map.put("foo.o", Arrays.asList("foo.h"));
        map.put("bar.o", Arrays.asList("bar.h", "foo.o"));
        map.put("bin", Arrays.asList("foo.o", "bar.o"));

        System.out.println(init("bin", map));

//        Map<String, List<String>> map2 = new HashMap<>();
//        map2.put("foo.h", new ArrayList<>());
//        map2.put("foo.t", new ArrayList<>());
//        map2.put("bar.h", Arrays.asList("foo.h"));
//        map2.put("bar.t", Arrays.asList("foo.t"));
//        map2.put("foo.o", Arrays.asList("foo.h"));
//        map2.put("bar.o", Arrays.asList("bar.h", "foo.o"));
//        map2.put("bin", Arrays.asList("foo.o", "bar.o"));

//        System.out.println(init("bin", map2));
//        System.out.println(onComplete("foo.h", map2));
    }
}
