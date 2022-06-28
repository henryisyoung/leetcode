package apple;

import java.util.HashMap;
import java.util.Map;

public class SnapshotArray {
    Map<Integer,Integer>[] A;
    int snap_id = 0;

    public SnapshotArray(int length) {
        A = new HashMap[length];
        for(int i=0; i<length;i++){
            A[i] = new HashMap<Integer,Integer>();
            A[i].put(0,0);
        }
    }

    public void set(int index, int val) {
        A[index].put(snap_id, val);
    }

    public int snap() {
        return snap_id++;
    }

    public int get(int index, int snap_id) {
        for(int i=snap_id;i>=0;i--){
            if(A[index].containsKey(i)){
                return A[index].get(i);
            }
        }
        return 0;
    }
}
