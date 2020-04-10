package facebook;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ExclusiveTimeOfFunctions {
    public static int[] exclusiveTime(int n, List<String> logs) {
        int[] result = new int[n];

        Stack<Integer> stack = new Stack<>();
        String[] first = logs.get(0).split(":");
        stack.push(Integer.parseInt(first[0]));
        int prevTime = Integer.parseInt(first[2]);

        for (int i = 1; i < logs.size(); i++) {
            String[] cur = logs.get(i).split(":");
            int curTime = Integer.parseInt(cur[2]);
            int curId = Integer.parseInt(cur[0]);

            boolean isStart = cur[1].equals("start");
            if (isStart) {
                if (!stack.isEmpty()) {
                    result[stack.peek()] += curTime - prevTime;
                }
                prevTime = curTime;
                stack.push(curId);
            } else {
                int id = stack.pop();
                result[id] += curTime - prevTime + 1;
                prevTime = curTime + 1;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(exclusiveTime(2, Arrays.asList("0:start:0","1:start:2","1:end:5","0:end:6"))));
    }
}
