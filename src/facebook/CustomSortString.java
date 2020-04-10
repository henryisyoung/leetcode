package facebook;

public class CustomSortString {
    public static String customSortString(String S, String T) {
        int[] t = new int[26];
        for (char c : T.toCharArray()) t[c - 'a']++;
        StringBuilder sb = new StringBuilder();
        for (char c : S.toCharArray()) {
            while (t[c - 'a'] > 0) {
                sb.append(c);
                t[c - 'a']--;
            }
        }
        for (char c = 'a'; c <= 'z'; c++) {
            while (t[c - 'a'] > 0) {
                sb.append(c);
                t[c - 'a']--;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String S = "exv" , T = "xwvee";
        StringBuilder sb = new StringBuilder();
        System.out.println(sb.append('a' * 7));
    }
}
