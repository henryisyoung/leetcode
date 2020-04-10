package facebook;

import java.util.Stack;

public class StringCompression {
    public static int compress(char[] chars) {
        int anchor = 0, write = 0;
        for (int read = 0; read < chars.length; read++) {
            if (read + 1 == chars.length || chars[read + 1] != chars[read]) {
                chars[write++] = chars[anchor];
                if (read > anchor) {
                    for (char c: Integer.toString(read - anchor + 1).toCharArray()) {
                        chars[write++] = c;
                    }
                }
                anchor = read + 1;
            }
        }
        return write;
    }
    public static boolean backspaceCompare(String S, String T) {
        Stack<Character> stack = new Stack<>();
        Stack<Character> stack2 = new Stack<>();
        for (char c : S.toCharArray()) {
            if (c == '#'){
                if (!stack.isEmpty()) stack.pop();
            }
            else stack.push(c);
        }
        for (char c : T.toCharArray()) {
            if (c == '#') {
                if (!stack2.isEmpty()) stack2.pop();
            }
            else stack2.push(c);
        }
        return stack.equals(stack2);
    }

    public static void main(String[] args) {
        char[] chars = {'a','b','b','b','b','b','b','b','b','b','b','b','b'};
//        System.out.println(compress(chars));
        System.out.println(backspaceCompare("y#fo##f", "y#f#o##f"));
    }
}
