package dropbox;

import java.util.*;

// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManager2 {
    Set<Integer> used;
    Queue<Integer> avaiableIDs;
    int maxID;
    public IdManager2(int maxId) {
        this.used = new HashSet<>();
        this.avaiableIDs = new LinkedList<>();
        this.maxID = maxId;
        for (int i = 0; i < maxId; i++) {
            avaiableIDs.add(i);
        }
    }

    /** Provide a id which is not assigned to anyone.
     @return - Return an available number. Return -1 if none is available. */
    public int allocate() {
        if (avaiableIDs.isEmpty()) return -1;
        int val = avaiableIDs.poll();
        used.add(val);
        return val;
    }

    /** Recycle or release a id. */
    public void release(int id) {
        if (id < 0 || id >= maxID) return;
        if (used.contains(id)) {
            used.remove(id);
            avaiableIDs.add(id);
        }
    }

    /** Check if a id is available or not. */
    public boolean check(int id) {
        if (id < 0 || id >= maxID) return false;
        return !used.contains(id);
    }
}
