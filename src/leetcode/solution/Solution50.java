package leetcode.solution;
import java.util.*;
public class Solution50 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution50 t = new Solution50();
		System.out.println(t.myPow(8.88023,3));
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("adc", "ads");
		map.put("adc", "adsss");
		map.put("adc", "hgssf");
		System.out.println(map.keySet());
		
	}
	public double myPow(double x,int n){
		if(n==0) return 1.00;
		if(n==1) return x;
		if(n<0){
			return 1/pow(x,-n);
		}else{
			return pow(x,n);
		}
	}
	private double pow(double x, int n) {
		if(n==0) return 1;
		double v=pow(x,n/2);
		if(n%2==0) return v*v;
		else return x*v*v;
	}

}
