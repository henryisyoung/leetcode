package databricks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
https://www.1point3acres.com/bbs/thread-873494-1-1.html
https://www.1point3acres.com/bbs/thread-907215-1-1.html
要求是可以达到一边iterate一边修改set elements而不影响iterator。
    set<int> s({1,2,3});
    set<int>::iterator iter = s.begin();
    while (iter != s.end()) {
        if (*iter == 1) {
            s.erase(1);
            s.insert(4);
        }
         print(*iter);
         iter++;
    }
上面的代码应该输出1 2 3但是再print一遍就是 2 3 4.

面试官不想用多余的空间
 */
public class MySnapshotSet {

    Map<Integer, Boolean> snapMap, curMap;
    public MySnapshotSet() {
        this.curMap = new HashMap<>();
        this.snapMap = new HashMap<>();
    }

    public void remove(int val) {
        if (snapMap.containsKey(val)) {
            snapMap.put(val, false);
        } else {
            curMap.remove(val);
        }
    }

    public void add(int val) {
        if (snapMap.containsKey(val)) {
            snapMap.put(val, true);
        } else {
            curMap.put(val, true);
        }
    }

    public boolean contains(int val) {
        if (snapMap.containsKey(val) && snapMap.get(val)) {
            return true;
        }
        if (curMap.containsKey(val)) {
            return true;
        }
        return false;
    }

    public MyIterator iterator() {
        snapMap = curMap;
        curMap = new HashMap<>();
        return new MyIterator(snapMap, curMap);
    }

    @Override
    public String toString() {
        return curMap.keySet().toString();
    }
}

class MyIterator implements Iterator<Integer> {
    Map<Integer, Boolean> snapMap, curMap;
    Iterator<Integer> iterator;

    public MyIterator(Map<Integer, Boolean> snapMap, Map<Integer, Boolean> curMap) {
        this.snapMap = snapMap;
        this.curMap = curMap;
        this.iterator = snapMap.keySet().iterator();
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = iterator.hasNext();
        if (hasNext == false) snapMap.clear();
        return hasNext;
    }

    @Override
    public Integer next() {
        int val = iterator.next();
        if (snapMap.get(val)) {
            curMap.put(val, true);
        }
        return val;
    }

    public static void main(String[] args) {
        MySnapshotSet set = new MySnapshotSet();
//        set.add(1);
//        set.add(2);
//        set.add(3);
//        MyIterator iterator = set.iterator();
//        while (iterator.hasNext()) {
//            int val = iterator.next();
//            if (val == 1) {
//                set.remove(1);
//                set.add(4);
//            }
//        }
//        System.out.println(set);
        set.add(5);
        set.add(2);
        set.add(8);
        set.remove(5);
        MyIterator iterator = set.iterator();
        System.out.println(set.contains(2));
        set.remove(2);
        System.out.println(set.contains(2));
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}

