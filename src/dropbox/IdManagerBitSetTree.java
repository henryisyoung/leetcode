package dropbox;

import java.util.BitSet;

// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManagerBitSetTree {

    private final int MAX_ID;
    private BitSet bitSet;

    public IdManagerBitSetTree(int maxId) {
        this.MAX_ID = maxId;
        this.bitSet = new BitSet(maxId*2-1);
    }

    public int allocate() {
        int index=0;
        while(index<MAX_ID-1) {
            if(!bitSet.get(index*2+1)) {
                index = index*2+1;
            } else if(!bitSet.get(index*2+2)) {
                index = index*2+2;
            } else {
                return -1;
            }
        }

        bitSet.set(index);
        updateTree(index);

        return index-MAX_ID+1;
    }

    public void updateTree(int index) {
        while(index>0) {
            int parent = (index-1)/2;
            if(index%2==1) { //left child
                if(bitSet.get(index) && bitSet.get(index+1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent); //it is required for release id
                }
            } else {
                if(bitSet.get(index) && bitSet.get(index-1)) {
                    bitSet.set(parent);
                } else {
                    bitSet.clear(parent);
                }
            }
            index = parent;
        }
    }

    public void release(int id) {
        if(id<0 || id>=MAX_ID) return;
        if(bitSet.get(id+MAX_ID-1)) {
            bitSet.clear(id+MAX_ID-1);
            updateTree(id+MAX_ID-1);
        }
    }

    public boolean check(int id) {
        if(id<0 || id>=MAX_ID) return false;
        return !bitSet.get(id+MAX_ID-1);
    }
}
