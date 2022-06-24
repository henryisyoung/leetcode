package apple;

import java.util.Iterator;

public class PeekingIterator implements Iterator<Integer> {
    Iterator<Integer> iterator;
    Integer cur;
    public PeekingIterator(Iterator<Integer> iterator) {
        // initialize any member here.
        this.iterator = iterator;
        cur = iterator.next();
    }

    // Returns the next element in the iteration without advancing the iterator.
    public Integer peek() {
        return cur;
    }

    // hasNext() and next() should behave the same as in the Iterator interface.
    // Override them if needed.
    @Override
    public Integer next() {
        int val = cur;
        cur = iterator.hasNext() ? iterator.next() : null;
        return val;
    }

    @Override
    public boolean hasNext() {
        return cur != null || iterator.hasNext();
    }
}
