package google.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CountWordsObtainedAfterAddingLetter {
    public int wordCount(String[] startWords, String[] targetWords) {
        Map<Integer, Set<Integer>> hashes = new HashMap();
        for (String word : startWords) {
            int l = word.length();
            if (!hashes.containsKey(l))
                hashes.put(l, new HashSet());

            hashes.get(l).add(hash(word));
        }

        int count = 0;
        for (String word : targetWords) {
            Set<Integer> h = hashes.get(word.length()-1);
            if (h == null)
                continue;

            for (int i = 0; i < word.length(); ++i) {
                String sub = word.substring(0, i) + word.substring(i+1);
                if (h.contains(hash(sub))) {
                    ++count;
                    break;
                }
            }
        }

        return count;
    }

    int hash(String word) {
        int res = 0;
        for (char c : word.toCharArray())
            res += 1 << c - 'a';

        return res;
    }
}
