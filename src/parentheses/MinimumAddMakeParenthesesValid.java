package parentheses;

public class MinimumAddMakeParenthesesValid {
    public static int minAddToMakeValid(String S) {
        int left = 0;
        int count = 0;
        for (char c : S.toCharArray()) {
            if (c == '(') {
                left++;
            } else {
                left--;
                if (left < 0) {
                    count++;
                    left = 0;
                }
            }
        }
        return count + left;
    }
    public static String minAddToMakeValidStr(String S) {
        int left = 0, n = S.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = S.charAt(i);
            if (c == '(') {
                left++;
                sb.append('(');
            } else {
                left--;
                if (left < 0) {
                    sb.append('(');
                    left = 0;
                }
                sb.append(')');
            }
        }
        while (left > 0) {
            sb.append(')');
            left--;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String S = "()))((";
        String S2 = "())";
        System.out.println(minAddToMakeValid(S));
        System.out.println(minAddToMakeValidStr(S));
        System.out.println(minAddToMakeValid(S2));
        System.out.println(minAddToMakeValidStr(S2));
    }
}
