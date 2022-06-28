package apple;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FreqStack {
    Map<Integer, Integer> freqMap;
    Map<Integer, Stack<Integer>> bucketMap;
    int maxFreq;
    public FreqStack() {
        this.bucketMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.maxFreq = 0;
    }

    public void push(int val) {
        freqMap.put(val, freqMap.getOrDefault(val, 0) + 1);
        int freq = freqMap.get(val);
        if (freq > maxFreq) {
            maxFreq = freq;
        }
        bucketMap.putIfAbsent(freq, new Stack<>());
        bucketMap.get(freq).add(val);
    }

    public int pop() {
        int val = bucketMap.get(maxFreq).pop();
        if (bucketMap.get(maxFreq).isEmpty()) {
            maxFreq--;
        }
        int freq = freqMap.get(val) - 1;
        if (freq == 0) {
            freqMap.remove(val);
        } else {
            freqMap.put(val, freq);
        }
        return val;
    }
}
