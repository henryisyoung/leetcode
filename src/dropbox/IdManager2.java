package dropbox;

import java.util.*;

// https://massivealgorithms.blogspot.com/2016/03/dropbox-interview-misc.html
//        allocate: Provide a number which is not assigned to anyone.
//        check: Check if a number is available or not.
//        release: Recycle or release a number.
public class IdManager2 {
    Set<Integer> used;
    Queue<Integer> available;
    int maxId;
    public IdManager2(int maxId) {
        this.maxId = maxId;
        this.used = new HashSet<>();
        this.available = new LinkedList<>();
        for (int i = 0; i < maxId; i++) {
            available.add(i);
        }
    }

    /** Provide a id which is not assigned to anyone.
     @return - Return an available number. Return -1 if none is available. */
    public int allocate() {
        if (available.isEmpty()) return -1;
        int val = available.poll();
        used.add(val);
        return val;
    }

    /** Recycle or release a id. */
    public void release(int id) {
        if (id >= maxId || id < 0) return;
        if (used.remove(id)) {
            available.add(id);
        }
    }

    /** Check if a id is available or not. */
    public boolean check(int id) {
        return !used.contains(id);
    }
}
