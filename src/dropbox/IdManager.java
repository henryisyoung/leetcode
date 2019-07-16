package dropbox;

import java.util.BitSet;
// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManager {
    BitSet bitSet;
    int maxId, nextAvailableId;

    public IdManager(int maxId) {
        this.maxId = maxId;
        this.nextAvailableId = 0;
        this.bitSet = new BitSet(maxId);
    }

    public int allocate() {
        if (nextAvailableId == maxId) return -1;
        int num = nextAvailableId;
        bitSet.set(nextAvailableId);
        nextAvailableId = bitSet.nextClearBit(num);
        return num;
    }

    public void release(int id) {
        if (id < 0 || id >= maxId) return;
        if (bitSet.get(id)) {
            bitSet.clear(id);
            nextAvailableId = Math.min(nextAvailableId, id);
        }
    }

    public boolean check(int id) {
        if (id < 0 || id >= maxId) return false;
        return !bitSet.get(id);
    }
}
