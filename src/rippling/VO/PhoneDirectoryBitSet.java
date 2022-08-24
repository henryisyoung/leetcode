package rippling.VO;

import java.util.*;

public class PhoneDirectoryBitSet {

    BitSet bitSet;
    int nextAvailable;
    int maxNumbers;
    public PhoneDirectoryBitSet(int maxNumbers) {
        this.bitSet = new BitSet(maxNumbers);
        this.nextAvailable = 0;
        this.maxNumbers = maxNumbers;
    }

    public int get() {
        if (nextAvailable >= maxNumbers) {
            return -1;
        }
        int val = nextAvailable;
        bitSet.set(nextAvailable);
        nextAvailable = bitSet.nextClearBit(nextAvailable);
        return val;
    }

    public boolean check(int number) {
        if (number < 0 || number >= maxNumbers) {
            return false;
        }
        return !bitSet.get(number);
    }

    public void release(int number) {
        if (!check(number)) {
            bitSet.clear(number);
            nextAvailable = Math.min(nextAvailable, number);
        }
    }
}
