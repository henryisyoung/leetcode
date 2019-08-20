package dropbox;

import java.util.BitSet;
import java.util.Map;
import java.util.regex.Matcher;

// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManager {
    BitSet bitSet;
    int maxId, next;
    public IdManager(int maxId) {
        this.bitSet = new BitSet(maxId);
        this.maxId = maxId;
        this.next = 0;
    }

    /** Provide a id which is not assigned to anyone.
     @return - Return an available number. Return -1 if none is available. */
    public int allocate() {
        if(next == maxId) return -1;
        int val = next;
        bitSet.set(next);
        next = bitSet.nextClearBit(next);
        return val;
    }

    /** Recycle or release a id. */
    public void release(int id) {
        if (id < 0 || id >= maxId) return;
        if (bitSet.get(id)) {
            bitSet.clear(id);
            next = Math.min(next, id);
        }
    }

    /** Check if a id is available or not. */
    public boolean check(int id) {
        if (id < 0 || id >= maxId) return false;
        return !bitSet.get(id);
    }
}
