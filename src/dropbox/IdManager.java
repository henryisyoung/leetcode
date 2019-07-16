package dropbox;

import java.util.BitSet;
// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManager {
    BitSet bitSet;
    int nextAvaiableId, maxId;

    public IdManager(int maxId) {
        this.maxId = maxId;
        this.nextAvaiableId = 0;
        this.bitSet = new BitSet(maxId);
    }

    /** Provide a id which is not assigned to anyone.
     @return - Return an available number. Return -1 if none is available. */
    public int allocate() {
        if (nextAvaiableId == maxId) return -1;
        int num = nextAvaiableId;
        bitSet.set(nextAvaiableId);
        nextAvaiableId = bitSet.nextClearBit(num);
        return num;
    }

    /** Recycle or release a id. */
    public void release(int id) {
        if (id < 0 || id >= maxId) return;
        if (bitSet.get(id)) {
            bitSet.clear(id);
            nextAvaiableId = Math.min(id, nextAvaiableId);
        }
    }

    /** Check if a id is available or not. */
    public boolean check(int id) {
        if (id < 0 || id >= maxId) return false;
        return !bitSet.get(id);
    }
}
