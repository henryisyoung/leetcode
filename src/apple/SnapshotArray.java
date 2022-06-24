package apple;

import java.util.HashMap;

public class SnapshotArray {
    int[] arr;
    int snapid_cur;
    HashMap<Integer, int[]> snapshots;


    public SnapshotArray(int length) {
        arr = new int[length];
        snapshots = new HashMap<Integer, int[]>();
        snapid_cur = -1;
    }

    public void set(int index, int val) {
        // set the value at index
        arr[index] = val;
    }

    public int snap() {
        // store a snapshot of the array at snapid_cur + 1
        ++snapid_cur;
        int[] tmp = arr.clone();
        snapshots.put(snapid_cur, tmp);
        return snapid_cur;
    }

    public int get(int index, int snap_id) {
        int[] list = snapshots.get(snap_id);
        return list[index];

    }
}
