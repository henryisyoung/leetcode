package leetcode;

public class Solution263 {

	public static void main(String[] args) {
		Solution263 t = new Solution263();
		System.out.println(t.isUgly(49));

	}
    public boolean isUgly(int num) {
        if(num < 1) return false;
        if(num == 1) return true;

        while(num != 1){
        	if(num % 2 == 0){
        		//System.out.println(num);
        		num /= 2;
        	}else if(num % 3 == 0){
        		num /= 3;
        	}else if(num % 5 == 0){
        		num /= 5;
        	}else{
        		
        		return false;
        	}
        }
        return num == 1;
    }
}
