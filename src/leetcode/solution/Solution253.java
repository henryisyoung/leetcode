package leetcode.solution;
import java.util.*;
public class Solution253 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    static class Point{
    int time, flag;
    	public static Comparator<Point> pointComp = new Comparator<Point>(){
    		public int compare(Point a, Point b){
	            if(a.time == b.time){
	                return a.flag - b.flag;
	            }
                return a.time - b.time;
            }
        };
			 
        public Point(int t, int f){
            this.time = t;
            this.flag = f;
        }
	}
	
    public int minMeetingRooms(Interval[] intervals) {
        if(intervals == null || intervals.length == 0){
        	return 0;
        }
        List<Point> list = new ArrayList<Point>();
        for(Interval in : intervals){
        	list.add(new Point(in.start, 1));
        	list.add(new Point(in.end, 0));
        }
        Collections.sort(list, Point.pointComp);
        int count = 0, max = 0;
        for(Point p : list){
        	if(p.flag == 1){
        		count ++;
        	}else{
        		count--;
        	}
        	max = Math.max(count, max);
        }
        return max;
    }
}
