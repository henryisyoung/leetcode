package rippling.VO;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class PhoneDirectory {
    Set<Integer> used;
    Queue<Integer> available;
    int maxNumbers;
    public PhoneDirectory(int maxNumbers) {
        this.maxNumbers = maxNumbers;
        this.available = new LinkedList<>();
        this.used = new HashSet<>();
        for (int i = 0; i < maxNumbers; i++) {
            available.add(i);
        }
    }

    public int get() {
        if (!available.isEmpty()) {
            int val = available.poll();
            used.add(val);
            return val;
        }
        return -1;
    }

    public boolean check(int number) {
        if (number >= maxNumbers || number < 0) {
            return false;
        }
        return !used.contains(number);
    }

    public void release(int number) {
        if (used.remove(number)) {
            available.add(number);
        }
    }
}
