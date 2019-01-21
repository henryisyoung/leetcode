package leetcode;

public class Solution38 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution38 t = new Solution38();
		System.out.println(t.countAndSay(0));
	}
	
    
    public String countAndSay(int n) {
    	if(n==1) return "1";
    	else{
            StringBuilder sb = new StringBuilder();
            int count =0;
            String str = countAndSay(n-1);
            for(int i=0;i<str.length();i++){
            	count++;
            	if(i+1>=str.length()||str.charAt(i)!=str.charAt(i+1)){
            		sb.append(count);
            		sb.append(str.charAt(i));
            		count=0;
            	}
            }
            return sb.toString();
    	}
    }

}
