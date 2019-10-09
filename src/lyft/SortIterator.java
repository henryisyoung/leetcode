package lyft;

import java.util.Iterator;
import java.util.List;

public class SortIterator implements Iterator<Integer> {
    List<Integer> list1, list2;
    int i, j, size1, size2;
    public SortIterator(List<Integer> list1, List<Integer> list2) {
        this.list1 = list1;
        this.list2 = list2;
        this.i = 0;
        this.j = 0;
        this.size1 = list1.size();
        this.size2 = list2.size();
    }
    @Override
    public boolean hasNext() {
        return i < size1 || j < size2;
    }

    @Override
    public Integer next() {
        if (i < size1 && j < size2) {
            if (list1.get(i) < list2.get(j)) {
                return list1.get(i++);
            } else {
                return list2.get(j++);
            }
        } else if (i < size1) {
            return list1.get(i++);
        } else {
            return list2.get(j++);
        }
    }
}
