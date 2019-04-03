package airbnb;

import java.util.*;
/*
Coding: 新题，给一个输入状态list，一个状态转移矩阵（有可能有多重转移状态，都需要考虑），一个最终接受状态集。实现一个自动机，每次读取一个状态i，
然后根据下一个状态i+1和转移矩阵决定输出状态，以此类推（当下的i+1状态下一次变成被读取的状态），这样处理一次长度为n的状态list得到长度n-1的list。
然后以新的状态list再进行转移。要求反复转移直到长度为1后，判断是否可以达到接受状态，如果有多种转移结果那么只要有一个成立即可。
举例：
"AB" -> "A"; "AA" -> ["A", "B"], "BA" -> "B", "BB" -> "A"，接受状态为["B"], 那么"ABAA" => ["ABA", "ABB"] => ["AB", "AA"] => ["A", "A", "B"] => OK.
这题不要想太多，按照描述的原理直接实现就好，注意，因为可能有重复的子状态list，所以一个优化是用memorization快速filter掉已经证明不行的子状态list。
 */
public class WordTransformer {
    public boolean checkWord(List<String> targets, Map<String, List<String>> map, String start) {
        if (targets == null || targets.size() == 0) {
            return false;
        }
        Map<String, Boolean> targetMap = new HashMap<>();
        for (String target : targets) {
            targetMap.put(target, true);
        }
        Map<String, Set<String>> transformMap = new HashMap<>();
        Set<String> next = findNextLevel(start, map);
        transformMap.put(start, next);
        for (String str : next) {
            if (dfsFindTarget(targetMap, transformMap, start, str, map)){
                return true;
            }
        }
        return false;
    }

    private boolean dfsFindTarget(Map<String, Boolean> targetMap, Map<String, Set<String>> transformMap, String from,
                                  String cur, Map<String, List<String>> map) {
        if (targetMap.containsKey(cur)) {
            targetMap.put(from, true);
            return true;
        }
        Set<String> nextSet = new HashSet<>();
        if (transformMap.containsKey(cur)) {
            nextSet = transformMap.get(cur);
        } else {
            nextSet = findNextLevel(cur, map);
        }
        transformMap.put(cur, nextSet);
        for (String next : nextSet) {
            if (dfsFindTarget(targetMap, transformMap, cur, next, map)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> findNextLevel(String start, Map<String, List<String>> map) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < start.length() - 1; i++) {
            String cur = start.substring(i, i + 2);
            if (map.containsKey(cur)) {
                for (String str : map.get(cur)) {
                    String s = start.substring(0, i) + str + start.substring(i + 2);
                    result.add(s);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> l1 = Arrays.asList("A"), l2 = Arrays.asList("A", "B"),
                l3 = Arrays.asList("B"), l4 = Arrays.asList("A");
        List<String> targets = Arrays.asList("B");
        List<String> strs = Arrays.asList("AB", "AA", "BA", "BB");
        map.put(strs.get(0), l1);
        map.put(strs.get(1), l2);
        map.put(strs.get(2), l3);
        map.put(strs.get(3), l4);
        WordTransformer sovler = new WordTransformer();
        Set<String> set = sovler.findNextLevel("ABAA", map);
        System.out.println("set " + set.toString());

        System.out.println(sovler.checkWord(targets, map, "ABAA"));
    }
}
