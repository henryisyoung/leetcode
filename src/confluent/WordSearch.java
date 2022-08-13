package confluent;

import java.util.*;

public class WordSearch {
    /*
    You are given a list of documents with id and text.
    Eg :-
    DocId, Text
    1, "Cloud computing is the on-demand availability of computer system resources."
    2, "One integrated service for metrics uptime cloud monitoring dashboards and alerts reduces time spent navigating between systems."
    3, "Monitor entire cloud infrastructure, whether in the cloud computing is or in virtualized data centers."

    Search a given phrase in all the documents in a efficient manner. Assume that you have more than 1 million docs.
    Eg :-
    search("cloud") >> This should output [1,2,3]
    search("cloud monitoring") >> This should output [2]
    search("Cloud computing is") >> This should output [1,3]


    第一问 给一个list of list [[1, "cloud computing"], ["2", "cloud confluent"]] 和一个string 比如 cloud
    这个list of list key叫做doc id， value叫做content
    要求implement一个search function return含有该string 的doc id list 例如这个case就return [1,2]
    需要达到optimize的程度就是多次调用 search(word) 这个api 能比较快
    第二问 将search function 改良， input 的string变成一个pharse 比如 cloud computing, 找到所有的 含有cloud computing 的 doc id list 这个case就return [2]
    我的做法就是 第一问做一次initial search 找到所有存在cloud的的case然后之后的search就比较optimize
    第二问 把第一问的intial search function改良了下 存下了doc id和index 然后再iterate 感‍‍觉不是想要的答案
     */

    public static Set<Integer> wordSearch(String[][] texts, String queryWord) {
        Map<String, Set<Integer>> invertedIndex = new HashMap<>();
        for (String[] text : texts) {
            int id = Integer.parseInt(text[0]);
            String strText = text[1];
            for (String word : strText.toLowerCase().split(" ")) {
                invertedIndex.putIfAbsent(word, new HashSet<>());
                invertedIndex.get(word).add(id);
            }
        }
        return invertedIndex.get(queryWord);
    }

    /*
    https://www.1point3acres.com/bbs/forum.php?mod=redirect&goto=findpost&ptid=811164&pid=17139843
     */

    public static Set<Integer> phaseSearch(String[][] texts, String queryPhase) {
        Map<String, Map<String, Set<Integer>>> invertedIndex = new HashMap<>();
        for (String[] text : texts) {
            int id = Integer.parseInt(text[0]);
            String[] words = text[1].toLowerCase().split(" ");

            for (int i = 0; i < words.length - 1; i++) {
                String a = words[i], b = words[i + 1];
                invertedIndex.putIfAbsent(a, new HashMap<>());
                invertedIndex.get(a).putIfAbsent(b, new HashSet<>());
                invertedIndex.get(a).get(b).add(id);
            }
        }
        String[] queryWords = queryPhase.split(" ");
        String a = queryWords[0], b = queryWords[1];
        if (!invertedIndex.containsKey(a) || !invertedIndex.get(a).containsKey(b)) {
            return new HashSet<>();
        }
        Set<Integer> candidates = invertedIndex.get(a).get(b);
        for (int i = 1; i < queryWords.length - 1; i++) {
            a = queryWords[i];
            b = queryWords[i + 1];
            if (!invertedIndex.containsKey(a) || !invertedIndex.get(a).containsKey(b)) {
                return new HashSet<>();
            }
            Set<Integer> cur = invertedIndex.get(a).get(b);
            candidates.retainAll(cur);
            if (candidates.isEmpty()) {
                return candidates;
            }
        }
        return candidates;
    }

    public static void main(String[] args) {
        String[][] texts ={
                {"1", "Cloud computing is the on-demand availability of computer system resources."},
                {"2", "One integrated service for metrics uptime cloud monitoring dashboards and alerts reduces time spent navigating between systems."},
                {"3", "Monitor entire cloud infrastructure, whether in the cloud computing is or in virtualized data centers."}

        };
        System.out.println(wordSearch(texts, "cloud"));
        System.out.println(phaseSearch(texts, "cloud computing is"));
    }
}
