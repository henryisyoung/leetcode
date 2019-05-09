package pinterest;

public class LongestCommonPrefix {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        int len = Integer.MAX_VALUE;
        String min = "";
        for (String str : strs) {
            if (str.length() < len) {
                min = str;
                len = str.length();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = min.charAt(i);
            for (String str : strs) {

                if (str.charAt(i) != c) {
                    return sb.toString();
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
