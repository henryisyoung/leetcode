package Bloomberg;

public class intToString {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		intToString t = new intToString();
		System.out.println(t.inttoString(Integer.MIN_VALUE ));
	}
	
	public String inttoString(int n){
		if(n == 0){
			return "0";
		}
		StringBuilder sb = new StringBuilder();
		if(n == Integer.MIN_VALUE){
			return Integer.MIN_VALUE + "";
		}
		int absN = Math.abs(n);
		while(absN > 0){
			sb.insert(0, absN % 10);
			absN /= 10;
		}
		if(n < 0){
			sb.insert(0, "-");
			return sb.toString();
		}else{
			return sb.toString();
		}
	}
}
