package leetcode.solution;

public class Solution66 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public int[] plusOne(int[] digits) {
    	int len = digits.length;
        for(int i=len-1;i>=0;i--){
        	if(digits[i]==9){
        		digits[i]=0;
        		if(i==0){
        			int[] newDigits = new int[len+1];
        			System.arraycopy(digits, 0, newDigits, 0, len);
        			newDigits[0]=1;
        			return newDigits;
        		}
        	}else{
        		digits[i]++;
        		break;
        	}
        }
        return digits;
    }

}
