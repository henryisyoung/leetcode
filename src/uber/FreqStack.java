package uber;

import java.util.*;

public class FreqStack {
    int max;
    Map<Integer, Integer> freqMap;
    Map<Integer, Stack<Integer>> groupMap;
    public FreqStack() {
        this.freqMap = new HashMap<>();
        this.groupMap = new HashMap<>();
        this.max = 0;
    }

    public void push(int x) {
        int freq = freqMap.getOrDefault(x, 0) + 1;
        freqMap.put(x, freq);
        max = Math.max(max, freq);
        groupMap.putIfAbsent(freq, new Stack<>());
        groupMap.get(freq).add(x);
    }

    public int pop() {
        Stack<Integer> stack = groupMap.get(max);
        int num = stack.pop();
        freqMap.put(num, max - 1);
        if (stack.isEmpty()) {
            groupMap.remove(max);
            max--;
        }

        return num;
    }

    public static void main(String[] args) {
        FreqStack solution = new FreqStack();
        solution.push(5);
        solution.push(7);
        solution.push(5);
        solution.push(7);
        solution.push(4);
        solution.push(5);
//        System.out.println(solution.pop());
//        System.out.println(solution.stack.toString());
//        System.out.println(solution.max);
        for (int i = 0; i < 4; i++) {
            System.out.println(solution.pop());
        }
    }
}
