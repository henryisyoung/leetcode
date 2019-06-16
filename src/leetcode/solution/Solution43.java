package leetcode.solution;

public class Solution43 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution43 t = new Solution43();
		System.out.println(t.multiply("217", "36"));
		
	}
    public String multiply(String num1, String num2) {
    	int m=num1.length(),n=num2.length(),zero=0;
    	int[] a = new int[m],c=new int[m+n];
    	for(int i=0,k=m-1;i<m;i++){
    		a[k--]=num1.charAt(i)-'0';
    	}
    	for(int i=n-1;i>=0;i--){
    		add(c,a,num2.charAt(i)-'0',zero++);
    	}
    	
    	carry(c);
    	int t=m+n;
    	while(t>0&&c[--t]==0);
        StringBuilder sb = new StringBuilder(t+1);
        for(int j=t;j>=0;j--) sb.append(c[j]); 
        return sb.toString();
    }
	private void carry(int[] c) {
		int tmp=0,d=0;
		for(int i=0;i<c.length;i++){
			tmp=c[i]+d;
			c[i]=tmp%10;
			d=tmp/10;
		}
	}
	private void add(int[] c, int[] a, int b, int zero) {
		for(int i=0,j=zero;i<a.length;i++,j++){
			c[j] += a[i]*b;
		}
	}
	 public String multiply2(String num1, String num2) { //slow
	        if (num1.equals("0") || num2.equals("0")) {
	            return "0";
	        }
	        int[] result = new int[num1.length() + num2.length()];
	        for (int i = num1.length() - 1; i >= 0; --i) {
	            for (int j = num2.length() - 1; j >= 0; --j) {
	                int contribution = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
	                int sum = result[i + j + 1] + contribution;
	                result[i + j + 1] = sum % 10;
	                result[i + j] += sum / 10;
	            }
	        }
	        StringBuilder ans = new StringBuilder();
	        for (int i = result[0] == 0 ? 1 : 0; i < result.length; ++i) {
	            ans.append(result[i]);
	        }
	        return ans.toString();
	    }
}
