package databricks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

// https://www.1point3acres.com/bbs/thread-887981-1-1.html
public class Solution {
    public static void main(String[] args) {
        List<Record> input = Arrays.asList(
                new Record("start batch"),
                new Record(11),
                new Record(55),
                new Record(99),
                new Record("start batch"),
                new Record("use alternate process"),
                new Record(100),
                new Record(50)
        );

        new Solution().calls(input.iterator());
    }

    public void processBatch(Iterator<Record> r, boolean alternate) {
        while (r.hasNext()) {
            System.out.printf("process %s alternative=%s%n", r.next().value, alternate);
        }
    }

    public void calls(Iterator<Record> it) {
        PeekingIterator<Record> peekingIterator = new PeekingIterator<>(it);
        while (peekingIterator.hasNext()) {
            peekingIterator.next();
            boolean alternate = peekingIterator.peek().type.equals("use alternate process");
            if (alternate) {
                peekingIterator.next();
            }
            processBatch(new StoppableIterator<>(peekingIterator, r -> r.type.equals("item")), alternate);
        }
    }
}

class StoppableIterator<T> implements Iterator<T> {
    private final PeekingIterator<T> iterator;
    private final Function<T, Boolean> valid;

    public StoppableIterator(PeekingIterator<T> iterator, Function<T, Boolean> valid) {
        this.iterator = iterator;
        this.valid = valid;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() && valid.apply(iterator.peek());
    }

    @Override
    public T next() {
        return iterator.next();
    }
}

class Record {
    public final String type;
    public final Integer value;

    Record(String type) {
        this.type = type;
        this.value = null;
    }

    Record(int value) {
        this.type = "item";
        this.value = value;
    }
}

class PeekingIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;
    private T cache = null;

    public PeekingIterator(Iterator<T> iterator) {
        this.iterator = iterator;
        populate();
    }

    private void populate() {
        if (iterator.hasNext()) {
            this.cache = iterator.next();
        } else {
            this.cache = null;
        }
    }

    public T peek() {
        return this.cache;
    }

    @Override
    public T next() {
        T val = cache;
        populate();
        return val;
    }

    @Override
    public boolean hasNext() {
        return cache != null;
    }
}