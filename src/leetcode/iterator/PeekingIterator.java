package leetcode.iterator;

import java.util.Iterator;

public class PeekingIterator implements Iterator<Integer> {
    Iterator<Integer> iterator;
    Integer cache;
    public PeekingIterator(Iterator<Integer> iterator) {
        // initialize any member here.
        this.iterator = iterator;
        cache = iterator.next();
    }

    // Returns the next element in the iteration without advancing the iterator.
    public Integer peek() {
        return cache;
    }

    // hasNext() and next() should behave the same as in the Iterator interface.
    // Override them if needed.
    @Override
    public Integer next() {
        int result = cache;
        cache = iterator.hasNext() ? iterator.next() : null;
        return result;
    }

    @Override
    public boolean hasNext() {
        return cache != null || iterator.hasNext();
    }
}
