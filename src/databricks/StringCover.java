package databricks;

import java.util.*;

public class StringCover {
    public List<List<Integer>> delete(String refString, List<List<Integer>> cover, int index) {
        StringBuilder sb = new StringBuilder();
        for (List<Integer> pair : cover) {
            // assume cover group is sorted and valid(no overlap)
            for (int i = pair.get(0); i < pair.get(1); i++) {
                sb.append(refString.charAt(i));
            }
        }
        String str = sb.toString();
        String deleted = str.substring(0, index) + str.substring(index + 1);
        return stringToCover(refString, deleted);
    }

    private List<List<Integer>> stringToCover(String refString, String str) {
        Map<Character, Integer> charIndex = new HashMap<>();
        char[] refArray = refString.toCharArray();
        for (int i = 0; i < refArray.length; i++) {
            charIndex.put(refArray[i], i);
        }
        List<List<Integer>> res = new ArrayList<>();
        char[] strArray = str.toCharArray();
        if (strArray.length < 1) {
            return res;
        }
        int firstIndex = charIndex.get(strArray[0]);
        List<Integer> current = new ArrayList<>(Arrays.asList(firstIndex, firstIndex + 1));
        for (int i = 1; i < strArray.length; i++) {
            int index = charIndex.get(strArray[i]);
            if (index != current.get(1)) {
                res.add(current);
                current = new ArrayList<>(Arrays.asList(index, index + 1));
            } else {
                current.set(1, index + 1);
            }
        }
        res.add(current);
        return res;
    }

    class Pair{
        int s, e;
        public Pair(int a, int b) {
            this.s = a;
            this.e = b;
        }
    }

    private List<Pair> stringToMaxCover(String refString, String str) {
        Map<Character, List<Integer>> charIndices = new HashMap<>();
        char[] refArray = refString.toCharArray();
        for (int i = 0; i < refArray.length; i++) {
            charIndices.computeIfAbsent(refArray[i], e -> new ArrayList<>()).add(i);
        }
        Queue<List<Pair>> queue = new LinkedList<>() ;
        char[] strArray = str.toCharArray();
        for (Integer i : charIndices.get(strArray[0])) {
            List<Pair> l = new ArrayList<>();
            l.add(new Pair(i, i + 1));
            queue.offer(l);
        }
        int cur = 1;
        while (!queue.isEmpty() && cur < strArray.length) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                List<Pair> cover = queue.poll();
                for (int curIndex : charIndices.get(strArray[cur])) {
                    List<Pair> copy = new ArrayList<>(cover);
                    Pair last = copy.get(copy.size() - 1);
                    if (last.e == curIndex) {
                        last.e = curIndex + 1;
                    } else {
                        copy.add(new Pair(curIndex, curIndex + 1));
                    }
                    queue.offer(copy);
                }
            }
            ++cur;
        }
        List<Pair> res = null;
        int shortest = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            List<Pair> cover = queue.poll();
            if (cover.size() < shortest) {
                res = cover;
                shortest = cover.size();
            }
        }
        return res;
    }


}