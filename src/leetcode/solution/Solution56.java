package leetcode.solution;

import java.util.*;

public class Solution56 {

	public static void main(String[] args) {
		Solution56 t = new Solution56();
		List<Interval> intervals = new ArrayList<Interval>();
		intervals.add(new Interval(1,3));
		intervals.add(new Interval(2,6));
		intervals.add(new Interval(8,10));
		intervals.add(new Interval(15,18));
		System.out.println(t.merge(intervals));

	}
    public List<Interval> merge(List<Interval> intervals) {
        if(intervals.size()<2||intervals==null) return intervals;
        List<Interval> list = new ArrayList<Interval>();
        Comparator<Interval> comp=new Comparator<Interval>(){
        	@Override
        	public int compare(Interval a,Interval b){
        		return a.start-b.start;
        	}
        };
        Collections.sort(intervals,comp);
        int start=intervals.get(0).start,end=intervals.get(0).end;
        for(int i=1;i<intervals.size();i++){
        	Interval cur=intervals.get(i);
        	if(cur.start>end){
        		list.add(new Interval(start,end));
        		start=cur.start;
        		end=cur.end;
        	}else{
        		end=Math.max(end, cur.end);
        	}
        }
        list.add(new Interval(start,end));
        return list;
    }
}
