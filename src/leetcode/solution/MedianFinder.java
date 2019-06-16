package leetcode.solution;
import java.util.*;
public class MedianFinder {
//	public PriorityQueue<Integer> minheap, maxheap;
//    public MedianFinder() {
//    	minheap = new PriorityQueue<Integer>();
//    	maxheap = new PriorityQueue<Integer>(Collections.reverseOrder());
//    }
//
//    // Adds a number into the data structure.
//    public void addNum(int num) {
//    	maxheap.add(num);
//    	minheap.add(maxheap.poll());
//    	if(maxheap.size() < minheap.size()){
//    		maxheap.add(minheap.poll());
//    	}
//    }
//
//    // Returns the median of current data stream
//    public double findMedian() {
//    	if(maxheap.size() == minheap.size()){
//    		return 0.5*(maxheap.peek() + minheap.peek());
//    	}else{
//    		return maxheap.peek();
//    	}
//    }
    PriorityQueue<Integer> min;
    PriorityQueue<Integer> max;

    /** initialize your data structure here. */
    public MedianFinder() {
        this.max = new PriorityQueue<Integer>((Collection<? extends Integer>) Collections.reverseOrder());
        this.min = new PriorityQueue<>();
    }

    public void addNum(int num) {
        max.add(num);
        min.add(max.poll());
        if(max.size() < min.size()) max.add(min.poll());
    }

    public double findMedian() {
        if(min.size() == max.size()){
            return 0.5 * (min.peek() + max.peek());
        }else return max.peek();
    }
}
