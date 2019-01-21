package leetcode;

public class Solution29 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution29 t = new Solution29();
		System.out.println(t.divide(-2147483648, -1));


	}
    public int divide(int dividend, int divisor) {
    	if(dividend==0) return 0;
    	if(divisor==0) return -1;
        
        long result =0;
        boolean flag = false;
        if(dividend<0 && divisor>0) flag=true;
        if(dividend>0 && divisor<0) flag=true;
        
        long did = Math.abs((long) dividend),dor=Math.abs((long) divisor);
        if(did<dor) return 0;
        while(dor<=did){
        	long tmp=dor;
        	long count = 1;
        	while(tmp<=did){
        		tmp = tmp << 1;
        		count=count<<1;
        	}
        	tmp=tmp>>1;
        	count=count>>1;
        	result+=count;
        	did=did-tmp;	
        }
        if(flag) return 0-(int) result;
        return (result>=Integer.MAX_VALUE)?Integer.MAX_VALUE:(int) result;
    }

}
