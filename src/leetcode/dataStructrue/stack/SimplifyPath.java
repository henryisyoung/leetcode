package leetcode.dataStructrue.stack;

import java.util.Stack;

public class SimplifyPath {
    public String simplifyPath(String path) {
        if (path == null || path.length() == 0) {
            return path;
        }
        String[] arr = path.split("/");
        Stack<String> stack = new Stack<>();
        for (String str : arr) {
            if (str.equals(".") || str.equals("")) {
                continue;
            } else if (str.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                stack.push(str);
            }
        }
        if(stack.isEmpty()) return "/";
        Stack<String> buffer = new Stack<>();
        while (!stack.isEmpty()) {
            buffer.push(stack.pop());
        }
        StringBuilder sb = new StringBuilder();
        while (!buffer.isEmpty()) {
            sb.append("/" + buffer.pop());
        }
        return sb.toString();
    }
}
