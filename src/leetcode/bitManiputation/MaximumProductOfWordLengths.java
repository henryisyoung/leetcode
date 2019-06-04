package leetcode.bitManiputation;

public class MaximumProductOfWordLengths {
    public int maxProduct(String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }
        int[] table = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                table[i] |= (1 << words[i].charAt(j) - 'a');
            }
        }
        int max = 0;
        for (int i = 0; i < words.length - 1; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if ((table[i] & table[j]) == 0 && words[i].length() * words[j].length() > max) {
                    max = words[i].length() * words[j].length();
                }
            }
        }
        return max;
    }
}
