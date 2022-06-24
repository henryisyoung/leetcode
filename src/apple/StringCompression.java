package apple;

public class StringCompression {
    public static int compress(char[] chars) {
        if (chars == null || chars.length == 0) {
            return 0;
        }
        int n = chars.length;
        int i = 0, j = 0;
        for (; j < n && i < n; i++) {
            char c = chars[j];
            int count = 0;
            while (j < n && chars[j] == c) {
                j++;
                count++;
            }
            if (count > 1) {
                chars[i++] = c;
                String str = Integer.toString(count);
                for (int t = 0; t < str.length(); t++) {
                    chars[i++] = str.charAt(t);
                }
                i--;
            } else {
                chars[i++] = c;
                i--;
            }
        }
        return i;
    }

    public static void main(String[] args) {
        System.out.println(compress(new char[]{'a','b','b','b','b','b','b','b','b','b','b','b','b',}));
        System.out.println(compress(new char[]{'a'}));
    }
}
