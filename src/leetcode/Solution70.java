package leetcode;
import java.util.*;
public class Solution70 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution70 t = new Solution70();
		System.out.println(t.climbStairsFast(4));

	}
	
    public int climbStairs(int n) {
        if(n<0) return 0;
        else if(n==0) return 1;
        else{
        	return climbStairs(n-1)+climbStairs(n-2);
        }
    }
    
    public int climbStairsFast(int n) { //hashtable
    	int[] ht = new int[n+1];
    	return chelper(n,ht);
    }

	private int chelper(int n, int[] ht) {
        if(n<0) return 0;
        else if(n==0) return 1;
        else if(ht[n]>0) return ht[n];
        else{
        	ht[n]=chelper(n-1,ht)+chelper(n-2,ht);
        	 return ht[n];
        }
	}
}
