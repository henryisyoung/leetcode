package leetcode.solution;

public class Solution12 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public String intToRoman(int num) {
    	/***
    	 *  I: 1
			IV: 4
			V: 5
			IX: 9
			X: 10
			XL: 40
			L: 50
			XC: 90
			C: 100
			CD: 400
			D: 500
			CM: 900
			M: 1000
    	 */
    	String[] data = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        int[] value = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        StringBuilder result = new StringBuilder();
        int base = -1;
        for(int i = 0;i < data.length;i++){
            if((base=num/value[i])!=0){
                while(base--!=0)result.append(data[i]);
                num=num%value[i];
            }
        }
        return result.toString();
        	
        }
    

}
