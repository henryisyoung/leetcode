//package leetcode;
//import java.util.*;
//public class PeekingIterator implements Iterator<Integer> {
//	Integer cache;
//	Iterator<Integer> it;
//	public PeekingIterator(Iterator<Integer> iterator) {
//	    // initialize any member here.
//		this.it = iterator;
//	    this.cache = iterator.next();
//	}
//
//    // Returns the next element in the iteration without advancing the iterator.
//	public Integer peek() {
//        return cache;
//	}
//
//	// hasNext() and next() should behave the same as in the Iterator interface.
//	// Override them if needed.
//	@Override
//	public Integer next() {
//	    int rlt = cache;
//	    cache = it.hasNext()?it.next():null;
//	    return rlt;
//	}
//
//	@Override
//	public boolean hasNext() {
//	    return cache != null || it.hasNext();
//	}
//}