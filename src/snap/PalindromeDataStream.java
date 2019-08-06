package snap;

import java.util.*;

public class PalindromeDataStream {
    public static int[] getStream2(String s) {
        int [] ans = new int[s.length()];
        int [] alphabet = new int[26];
        int cnt = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (alphabet[c - 'a'] % 2 == 1) {
                cnt--;
                alphabet[c - 'a']++;
            } else {
                cnt++;
                alphabet[c - 'a']++;
            }
            if (cnt > 1) {
                ans[i] = 0;
            } else {
                ans[i] = 1;
            }
        }
        return ans;
    }
    public static int[] getStream(String s) {
        if (s == null || s.length() == 0) return new int[0];
        int n = s.length();
        int[] result = new int[n];
        Arrays.fill(result, 1);
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            map.put(c, map.getOrDefault(c, 0) + 1);
            boolean isOdd = false;
            for (Map.Entry<Character, Integer> entry : map.entrySet()) {
                if (entry.getValue() % 2 == 1) {
                    if (isOdd) {
                        result[i] = 0;
                        break;
                    }
                    isOdd = true;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String s = "aba";
        int[] result = getStream(s);
        System.out.println(Arrays.toString(result));
    }
}
