package google.vo;

import java.util.Arrays;
import java.util.Stack;

public class NumberWeakCharacters {
    public int numberOfWeakCharacters(int[][] properties) {
        Arrays.sort(properties, (a, b) -> {
            if (a[0] == b[0]) {
                return b[1] - a[1];
            }
            return a[0] - b[0];
        });
        Stack<int[]> stack = new Stack<>();
        for (int[] prop : properties) {
            while (!stack.isEmpty() && compare(stack.peek(), prop)) {
                stack.pop();
            }
            stack.push(prop);
        }
        return properties.length - stack.size();
    }

    private boolean compare(int[] peek, int[] prop) {
        return peek[0] < prop[0] && peek[1] < prop[1];
    }
}
