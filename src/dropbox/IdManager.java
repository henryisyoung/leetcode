package dropbox;

import java.util.BitSet;
// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
public class IdManager {
    private final int MAX_ID;
    private BitSet bitSet;
    private int nextAvailable;

    public IdManager(int maxId) {
        this.MAX_ID = maxId;
        this.bitSet = new BitSet(maxId);
        this.nextAvailable = 0;
    }

    public int allocate() {
        if(nextAvailable == MAX_ID) return -1;
        int num = nextAvailable;
        bitSet.set(num);
        nextAvailable = bitSet.nextClearBit(num);
        return num;
    }

    public void release(int id) {
        if(id < 0 || id >= MAX_ID) return;
        if(bitSet.get(id)) {
            bitSet.clear(id);
            nextAvailable = Math.min(nextAvailable, id);
        }
    }

    public boolean check(int id) {
        if(id < 0 || id >= MAX_ID) return false;
        return !bitSet.get(id);
    }
}
