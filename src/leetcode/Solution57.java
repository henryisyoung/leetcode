package leetcode;

import java.util.*;

public class Solution57 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public List<Interval> insert(List<Interval> intervals, Interval newInterval) {
    	List<Interval> list = new ArrayList<Interval>();
    	if(intervals.size()==0||intervals==null){
    		list.add(newInterval);
    		return list;
    	}
    	int loc=0;
    	for(Interval cur:intervals){
    		if(cur.end<newInterval.start){
    			list.add(cur);
    			loc++;
    		}else if(cur.start>newInterval.end){
    			list.add(cur);
    		}else{
    			newInterval.start=Math.min(newInterval.start, cur.start);
    			newInterval.end=Math.max(newInterval.end, cur.end);
    		}
    	}
    	list.add(loc,newInterval);
    	return list;
    }

}
