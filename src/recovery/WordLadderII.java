package recovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordLadderII {
    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> result = new ArrayList<>();
        if (!wordList.contains(endWord)) {
            return result;
        }

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> level = new HashMap<>();
        if (!canBuildGraph(graph, beginWord, endWord, wordList, level)) {
            return result;
        }

        dfsFindAll(graph, beginWord, endWord, wordList, level, result, new ArrayList<>());
        return result;
    }

    private void dfsFindAll(Map<String, List<String>> graph, String curWord, String endWord, List<String> wordList, Map<String, Integer> level, List<List<String>> result, ArrayList<String> list) {

    }

    private boolean canBuildGraph(Map<String, List<String>> graph, String beginWord, String endWord, List<String> wordList, Map<String, Integer> level) {

    }
}
