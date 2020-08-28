package apple;

import java.util.Iterator;

class PeekingIterator<T> implements Iterator<T> {
    private Iterator<T> iterator;
    private T cur;
    public PeekingIterator(Iterator<T> iterator) {
        // initialize any member here.
        this.iterator = iterator;
        this.cur = iterator.next();
    }

    // Returns the next element in the iteration without advancing the iterator.
    public T peek() {
        return cur;
    }

    // hasNext() and next() should behave the same as in the Iterator interface.
    // Override them if needed.
    @Override
    public T next() {
        T result = cur;
        cur = iterator.hasNext() ? iterator.next() : null;
        return result;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() || cur != null;
    }
}
