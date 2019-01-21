package interview;

public class findLoc {
	public static void main(String [] args){
		//String a = "ababacbabab";
		//String b = "ababababab";
		//String a = "ababababab";
		//String b = "ababababacb";
		String a = "ababababacb";
		String b = "ababadbabacb";
		System.out.println(new findLoc().findInsertChar(a, b, Math.max(a.length(),b.length())));
	}
	
	public char findInsertChar(String a, String b, int n){//n是最长字符串长度
		int start = 0, end = n - 2;
		while(start <= end){
		 	int mid = (end - start)/2 + start;
		 	if(a.charAt(mid) == b.charAt(mid)){
				start = mid + 1;  
		 	}
			else{
				end = mid -1;
		 	}
		}
		if(start == n-1){
			return a.length() > b.length() ? a.charAt(n-1) : b.charAt(n-1);
		}
		if(a.length() > b.length()){
			return (b.charAt(start) == a.charAt(start+1)) ? a.charAt(start) : b.charAt(start); 
		}
		return (a.charAt(start) == b.charAt(start+1)) ? b.charAt(start) : a.charAt(start); 
	 }
}
