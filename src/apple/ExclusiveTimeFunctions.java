package apple;

import java.util.List;
import java.util.Stack;

public class ExclusiveTimeFunctions {
    public int[] exclusiveTime(int n, List<String> logs) {
        int[] result = new int[n];

        Stack<Integer> stack = new Stack<>();
        String[] first = logs.get(0).split(":");

        int prevId = Integer.parseInt(first[0]), prevTime = Integer.parseInt(first[2]);
        stack.push(prevId);
        for(int i = 1; i < logs.size(); i++) {
            String[] cur = logs.get(i).split(":");
            int curId = Integer.parseInt(cur[0]), curTime = Integer.parseInt(cur[2]);
            String start = cur[1];
            if(start.equals("start")) {
                if(!stack.isEmpty()) {
                    result[stack.peek()] += curTime - prevTime;
                }
                prevTime = curTime;
                stack.push(curId);
            } else {
                result[stack.pop()] += curTime - prevTime + 1;
                prevTime = curTime + 1;
            }
        }
        return result;
    }
}
