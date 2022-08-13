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
public class MySnapshotSet2 {

    Map<Integer, Boolean> snapMap, curMap;

    public MySnapshotSet2() {
        this.curMap = new HashMap<>();
        this.snapMap = new HashMap<>();
    }

    public void add(int num) {
        if (snapMap.containsKey(num)) {
            snapMap.put(num, true);
        }
        curMap.put(num, true);
    }

    public void remove(int num) {
        if (snapMap.containsKey(num)) {
            snapMap.put(num, false);
        }
        curMap.remove(num);
    }

    public boolean contains(int num) {
        if (snapMap.containsKey(num)) {
            return snapMap.get(num);
        }
        if (curMap.containsKey(num)) {
            return curMap.get(num);
        }
        return false;
    }

    public SnapIterator iterator() {
        snapMap = new HashMap<>(curMap);
        return new SnapIterator(snapMap);
    }

    class SnapIterator implements Iterator {

        Map<Integer, Boolean> snapMap;
        Iterator<Integer> iterator;
        public SnapIterator(Map<Integer, Boolean> snapMap) {
            this.snapMap = snapMap;
            this.iterator = snapMap.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = iterator.hasNext();
            if (!hasNext) {
                snapMap.clear();
            }
            return hasNext;
        }

        @Override
        public Integer next() {
            Integer next = iterator.next();
            return next;
        }
    }

    public static void main(String[] args) {
//        MySnapshotSet2 set = new MySnapshotSet2();
//        set.add(1);
//        set.add(2);
//        set.add(3);
//        MyIterator2 iterator = set.iterator();
//        while (iterator.hasNext()) {
//            int val = iterator.next();
//            System.out.println("val " + val);
//            if (val == 1) {
//                set.remove(1);
//                set.add(4);
//            }
//        }
//        System.out.println(set);
    }
}

