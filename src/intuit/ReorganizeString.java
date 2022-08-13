package intuit;

public class ReorganizeString {
    public String reorganizeString(String s) {
        int[] counter = new int[26];
        int maxCount = 0;
        int max = -1;
        for (char c: s.toCharArray()) {
            counter[c - 'a']++;
            if (counter[c - 'a'] > maxCount) {
                max = c - 'a';
                maxCount = counter[c - 'a'];
            }
        }
        int size = s.length();
        if (maxCount > (size + 1) / 2) {
            return "";
        }
        int i = 0;
        char[] result = new char[size];
        while (counter[max] > 0) {
            counter[max]--;
            result[i] = (char) (max + 'a');
            i += 2;
        }
        for (int c = 0; c < 26; c++) {
            while (counter[c] > 0) {
                if (i >= size) {
                    i = 1;
                }
                counter[c]--;
                result[i] = (char) (c + 'a');
                i += 2;
            }
        }
        return new String(result);
    }
}
