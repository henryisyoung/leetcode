package google;

import java.util.*;

public class PhoneDirectory {
    /** Initialize your data structure here
     @param maxNumbers - The maximum numbers that can be stored in the phone directory. */
    int maxNumbers;
    Set<Integer> usedNumbers;
    Queue<Integer> avaiableNumbers;
    public PhoneDirectory(int maxNumbers) {
        this.maxNumbers = maxNumbers;
        this.usedNumbers = new HashSet<>();
        this.avaiableNumbers = new LinkedList<>();
        for (int i = 0; i < maxNumbers; i++) {
            avaiableNumbers.add(i);
        }
    }

    /** Provide a number which is not assigned to anyone.
     @return - Return an available number. Return -1 if none is available. */
    public int get() {
        if (avaiableNumbers.isEmpty()) {
            return -1;
        }
        int val = avaiableNumbers.poll();
        usedNumbers.add(val);
        return val;
    }

    /** Check if a number is available or not. */
    public boolean check(int number) {
        if (number < 0 || number >= maxNumbers) {
            return false;
        }
        return !usedNumbers.contains(number);
    }

    /** Recycle or release a number. */
    public void release(int number) {
        if (usedNumbers.remove(number)) {
            avaiableNumbers.offer(number);
        }
    }
}
