package uber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UberIterator implements Iterator<Integer> {
    Iterator<Integer> iterator;
    public UberIterator(Iterator<Iterator<Integer>> its){
        this.iterator = buildIterator(its);
    }

    private Iterator<Integer> buildIterator(Iterator<Iterator<Integer>> its) {
        List<Integer> list = new ArrayList<>();
        while (its.hasNext()){
            Iterator<Integer> it2 = its.next();
            while (it2.hasNext()) {
                list.add(it2.next());
            }
        }
        return list.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Integer next() {
        return iterator.next();
    }
}
