package dropbox;

import java.util.BitSet;

// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManagerBitSetSegmentTree {
    BitSet bitSet;
    int maxId;
    public IdManagerBitSetSegmentTree(int maxId) {
        this.bitSet = new BitSet(2 * maxId - 1);
        this.maxId = maxId;
    }

    public int allocate() {
        int start = 0;
        while (start < maxId - 1) {
            if (!bitSet.get(start * 2 + 1)) {
                start = start * 2 + 1;
            } else if (!bitSet.get(start * 2 + 2)) {
                start = start * 2 + 2;
            } else {
                return -1;
            }
        }
        if (bitSet.get(start)) return -1;
        bitSet.set(start);
        updateTree(start);
        return start - maxId + 1;
    }

    private void updateTree(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (index % 2 == 1) {
                if(bitSet.get(index) && bitSet.get(index + 1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent);
                }
            } else {
                if(bitSet.get(index) && bitSet.get(index - 1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent);
                }
            }
            index = parent;
        }
    }


    public void release(int id) {
        if (id < 0 || id >= maxId) return;
        if (bitSet.get(id + maxId - 1)) {
            bitSet.clear(id + maxId - 1);
            updateTree(id + maxId - 1);
        }
    }

    public boolean check(int id) {
        if (id < 0 || id >= maxId) return false;
        return !bitSet.get(id + maxId - 1);
    }
}