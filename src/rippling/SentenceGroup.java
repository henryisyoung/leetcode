package rippling;

import leetcode.union.Union;

import java.util.*;

public class SentenceGroup {
    /*
    遇到新题了，题感觉也不简单, 就当攒人品 move on 了
给一个list of 同义词 和 一个list of sentences， 把相似的sentence group到一起
ie：input: [("main", "primary"), ("main", "important"), ("rating", "score")]
["main email" , "secondary email", "primary email", "important email", "performance rating", "performance score"]
output: [ ["main email", "primary email", "important email"], ["secondary email"], ["performance rating", "performance score"] ]
注意 相似的sentence是指这个sentence中每个word要么相同要么是同义词，比较的时候只要比较每个word就行了
     */

    public static List<List<String>> sentenceGroup(String[][] words, String[] sentences) {
        Map<Integer, String> idToWord = new HashMap<>();
        Map<String, Integer> wordToId = new HashMap<>();
        int id = 0;
        for (String[] wordPair : words) {
            String a = wordPair[0], b = wordPair[1];
            if (!wordToId.containsKey(a)) {
                wordToId.put(a, id);
                idToWord.put(id++, a);
            }
            if (!wordToId.containsKey(b)) {
                wordToId.put(b, id);
                idToWord.put(id++, b);
            }
        }
        int size = wordToId.size();
        Union union = new Union(size);
        for (String[] wordPair : words) {
            String a = wordPair[0], b = wordPair[1];
            int aid = wordToId.get(a), bid = wordToId.get(b);
            union.union(aid, bid);
        }

        int n = sentences.length;
        Set<Integer> set = new HashSet<>();
        Map<Integer, Set<String>> map = new HashMap<>();
        for (int i = 0; i < n - 1; i++) {
            String a = sentences[i];
            for (int j = i + 1; j < n; j++) {
                String b = sentences[j];
                int parent = similarSentence(a.split(" "), b.split(" "), wordToId, union);
                if (parent != -1) {
                    map.putIfAbsent(parent, new HashSet<>());
                    map.get(parent).add(a);
                    map.get(parent).add(b);
                    set.add(i);
                    set.add(j);
                }
            }
        }
        List<List<String>> result = new ArrayList<>();
        for (Set<String> val : map.values()) {
            result.add(new ArrayList<>(val));
        }
        for (int i = 0; i < sentences.length; i++) {
            if (!set.contains(i)) {
                result.add(Arrays.asList(sentences[i]));
            }
        }
        return result;
    }

    private static int similarSentence(String[] senA, String[] senB, Map<String, Integer> wordToId, Union union) {
        if (senA.length != senB.length) {
            return -1;
        }
        int parent = -1;
        int count = 0;
        for (int k = 0; k < senA.length; k++) {
            if (!senA[k].equals(senB[k])) {
                if (count > 0) {
                    return -1;
                }
                count++;
                Integer aId = wordToId.get(senA[k]), bId = wordToId.get(senB[k]);
                if (aId == null || bId == null) {
                    return -1;
                }
                int fa = union.find(aId), fb = union.find(bId);
                if (fa != fb) {
                    return -1;
                }
                parent = fa;
            }
        }
        return parent;
    }


    public static void main(String[] args) {
//        String[][] words = {{"main", "primary"}, {"main", "important"}, {"rating", "score"}};
//        String[] sentence = {"main email" , "secondary email", "primary email",
//                "important email", "performance rating", "performance score"};
//        System.out.println(sentenceGroup(words, sentence));

        System.out.println("abc".contains("bc"));
    }

}
