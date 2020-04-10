package parentheses;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MinimumRemoveMakeValidParentheses {
    public String minRemoveToMakeValid(String s) {
        Set<Integer> remove = new HashSet<>();
        Stack<Integer> leftIndex = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                leftIndex.add(i);
            } else if (c == ')') {
                if (leftIndex.isEmpty()) {
                    remove.add(i);
                } else {
                    leftIndex.pop();
                }
            }
        }
        remove.addAll(leftIndex);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (remove.contains(i)) continue;
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}
