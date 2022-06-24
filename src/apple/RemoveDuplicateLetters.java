package apple;

import java.util.Stack;

public class RemoveDuplicateLetters {
    public String removeDuplicateLetters(String s) {
        int[] count = new int[256];
        boolean[] visited = new boolean[256];
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            count[c]++;
        }

        for (char c : s.toCharArray()) {
            count[c]--;
            if (visited[c]) continue;

            while (!stack.isEmpty() && c < stack.peek() && count[stack.peek()] > 0) {
                visited[stack.pop()] = false;
            }
            stack.push(c);
            visited[c] = true;
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }
        return sb.reverse().toString();
    }
}
