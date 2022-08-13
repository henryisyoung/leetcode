package google.vo;

import java.util.ArrayList;
import java.util.List;

public class GuessWord {
    class Master {
        public int guess(String word) {
            return -1;
        }
    }

    public void findSecretWord(String[] wordlist, Master master) {
        for (int i = 0, count = 0; i < 10 && count < 6; i++) {
            String str = wordlist[0];
            count = master.guess(str);
            List<String> list = new ArrayList<>();
            for (String word : wordlist) {
                if (countMathc(word, str) == count) {
                    list.add(word);
                }
            }
            wordlist = list.toArray(new String[list.size()]);
        }
    }

    private int countMathc(String word, String str) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }
}
