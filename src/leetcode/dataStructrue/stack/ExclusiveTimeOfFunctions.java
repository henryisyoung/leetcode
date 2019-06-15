package leetcode.dataStructrue.stack;

import java.util.*;

public class ExclusiveTimeOfFunctions {
    public static int[] exclusiveTime(int n, List<String> logs) {
        if(n == 0 ) {
            return new int[0];
        }
        int[] result = new int[n];
        Stack<Integer> stack = new Stack<>();
        String[] cur = logs.get(0).split(":");
        stack.push(Integer.parseInt(cur[0]));
        int time = Integer.parseInt(cur[2]);

        int i = 1, size = logs.size();
        while (i < size) {
            cur = logs.get(i).split(":");
            while (time < Integer.parseInt(cur[2])) {
                result[stack.peek()]++;
                time++;
            }
            if (cur[1].equals("start")) {
                stack.push(Integer.parseInt(cur[0]));
            } else {
                result[stack.peek()]++;
                time++;
                stack.pop();
            }
            i++;
        }
        System.out.println(Arrays.toString(result));
        return result;
    }

    public static int[] exclusiveTime2(int n, List < String > logs) {
        Stack < Integer > stack = new Stack < > ();
        int[] res = new int[n];
        String[] s = logs.get(0).split(":");
        stack.push(Integer.parseInt(s[0]));
        int i = 1, prev = Integer.parseInt(s[2]);

        while (i < logs.size()) {
            s = logs.get(i).split(":");
            if (s[1].equals("start")) {
                if (!stack.isEmpty()) {
                    res[stack.peek()] += Integer.parseInt(s[2]) - prev;
                }
                stack.push(Integer.parseInt(s[0]));
                prev = Integer.parseInt(s[2]);
            } else {
                res[stack.peek()] += Integer.parseInt(s[2]) - prev + 1;
                stack.pop();
                prev = Integer.parseInt(s[2]) + 1;
            }
            i++;
        }
        return res;
    }

    public static void main(String[] args) {
        int n = 2;
        List<String> logs = Arrays.asList("0:start:0","1:start:2","1:end:5","0:end:6");
        exclusiveTime(n, logs);
    }
}
