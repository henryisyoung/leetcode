package leetcode.solution;
import java.util.*;
public class TwoSum {

    HashMap<Integer, Integer> map;
    public TwoSum() {
        map = new HashMap<Integer, Integer>();
    }
    
    public void add(int x) {
        if (map.containsKey(x)) {
            map.put(x, map.get(x)+1);
        }
        else {
            map.put(x, 1);
        }
    }
    
    public boolean find(int target) {
        for (int i : map.keySet()) {
            if (map.containsKey(target-i)) {
                if (target - i != i) return true;
                else if (map.get(i) >= 2) return true;
            }
        }
        return false;
    }
}
