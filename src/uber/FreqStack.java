package uber;

import java.util.*;
import java.util.function.Function;

public class FreqStack {
    Map<Integer, Integer> freqMap;
    Map<Integer, Stack<Integer>> groupMap;
    int maxFreq;
    public FreqStack() {
        this.maxFreq = 0;
        this.freqMap = new HashMap<>();
        this.groupMap = new HashMap<>();
    }

    public void push(int x) {
        freqMap.put(x, freqMap.getOrDefault(x, 0) + 1);
        maxFreq = Math.max(maxFreq, freqMap.get(x));
        groupMap.putIfAbsent(freqMap.get(x), new Stack<>());
        groupMap.get(freqMap.get(x)).push(x);
    }

    public int pop() {
        int val = groupMap.get(maxFreq).pop();
        freqMap.put(val, freqMap.get(val) - 1);
        if (groupMap.get(maxFreq).size() == 0) {
            maxFreq--;
        }
        return val;
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
