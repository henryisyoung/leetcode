package dropbox;

import java.util.BitSet;

// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManagerBitSetTree {
    BitSet bitSet;
    int maxId;
    public IdManagerBitSetTree(int maxId) {
       this.maxId = maxId;
       this.bitSet = new BitSet(2 * maxId - 1);
    }

    public int allocate() {
        int index = 0;
        while (index < maxId - 1) {
            if (!bitSet.get(index * 2 + 1)) {
                index = index * 2 + 1;
            } else if (!bitSet.get(index * 2 + 2)) {
                index = index * 2 + 2;
            } else {
                return -1;
            }
        }
        bitSet.set(index);
        updateTree(index);
        return index - maxId + 1;
    }

    private void updateTree(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (index % 2 == 1) {
                if (bitSet.get(index) && bitSet.get(index + 1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent);
                }
            } else {
                if (bitSet.get(index) && bitSet.get(index - 1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent);
                }
            }
            index = parent;
        }
    }


    public void release(int id) {
        if(id < 0 || id >= maxId) return;
        if (bitSet.get(id + maxId - 1)) {
            bitSet.clear(id + maxId - 1);
            updateTree(id + maxId - 1);
        }
    }

    public boolean check(int id) {
        if(id < 0 || id >= maxId) return false;
        return !bitSet.get(id + maxId - 1);
    }
}
