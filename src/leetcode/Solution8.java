package leetcode;

public class Solution8 {
	/***
	 *  1. null or empty string
		2. white spaces
		3. +/- sign
		4. calculate real value
		5. handle min & max
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution8 t = new Solution8();
		int a = t.myAtoi("9223372036854775809");
		System.out.println(a);
	}
    public int myAtoi(String str) {
        if(str.length()<1 || str==null) return 0;
        char flag = '+';
        double sum = 0;  // long does not work here need double
        str = str.trim();
        int i = 0;
        if(str.charAt(0)==flag) i++;
        if(str.charAt(0)=='-'){
        	flag = '-';
        	i++;
        }
        while(i<str.length() && str.charAt(i)>='0' && str.charAt(i)<='9'){
        	sum = 10*sum + (str.charAt(i) - '0');
        	i++;
        }
    	System.out.println(sum);

        if(flag=='-') sum = sum*-1;
        if(sum>Integer.MAX_VALUE) sum=Integer.MAX_VALUE;
        if(sum<Integer.MIN_VALUE) sum=Integer.MIN_VALUE;
        return (int) sum;
    }
    
    public int myAtoiRevised(String str) {
        if(str.length()<1 || str==null) return 0;
        char flag = '+';
        double sum = 0;
        str = str.trim();
        int i = 0;
        if(str.charAt(0)==flag) i++;
        if(str.charAt(0)=='-'){
        	flag = '-';
        	i++;
        }
        while(i<str.length() && str.charAt(i)>='0' && str.charAt(i)<='9'){
        	sum = 10*sum + (str.charAt(i) - '0');
        	if(sum>Integer.MAX_VALUE) return (flag=='+')?Integer.MAX_VALUE:Integer.MIN_VALUE; 
        	//stop advance! Caution	sum>Integer.MAX_VALUE no equal Because the negative case			
        	i++;
        }
        if(flag=='-') sum = sum*-1;
        return (int) sum;
    }
    
    public int myAtoi2(String str) {
        if(str.isEmpty())
            return 0;
        str=str.trim();
        int i=0,ans=0,sign=1,len=str.length();
        if(str.charAt(i)=='-'||str.charAt(i)=='+')
            sign=str.charAt(i++)=='+'?1:-1;
        for(;i<len;++i){
            int tmp=str.charAt(i)-'0';
            if(tmp<0||tmp>9)
                break;
            if(ans>Integer.MAX_VALUE/10||ans==Integer.MAX_VALUE/10&&Integer.MAX_VALUE %10 < tmp)
            	// do not need finish iterate all n digits
                return sign==1?Integer.MAX_VALUE:Integer.MIN_VALUE;
            else
                ans=ans*10+tmp;
        }
        return sign*ans;
    }

}
