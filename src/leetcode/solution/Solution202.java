package leetcode.solution;
import java.util.*;
public class Solution202 {
    public boolean isHappy(int n) {
    	if(n <= 0) return false;
        HashSet<Integer> set = new HashSet<Integer>();
        while(n != 1){
        	n = createNew(n);
        	if(set.contains(n)){
        		return false;
        	}else{
        		set.add(n);
        	}
        }
        return true;
    }

	private int createNew(int n) {
		int sum = 0;
		while(n > 0){
			sum += (n % 10)* (n % 10);
			n /= 10;
		}
		return sum;
	}
}
