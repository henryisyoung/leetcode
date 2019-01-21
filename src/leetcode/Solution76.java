package leetcode;

public class Solution76 {

	public static void main(String[] args) {
		String s = "ADOBECODEBANC";
		String tt = "ABCW";
		Solution76 t = new Solution76();
		System.out.println(t.minWindow(s, tt));
	}
	
	
    public String minWindow(String s, String t) {
        String minStr = "";
        if(s.length() < t.length()){
        	return minStr;
        }
        int[] targetHash = new int[256], sourceHash = new int[256];
        
        for(char c : t.toCharArray()){
        	targetHash[c]++;
        }
        
        int j = 0, len = Integer.MAX_VALUE, n = s.length();
        for(int i = 0; i < n; i++){
        	while(j < n && !isValid(targetHash, sourceHash)){
        		sourceHash[s.charAt(j++)]++;
        	}
        	if(isValid(targetHash, sourceHash)){
        		if(len > j - i){
        			len = j - i;
        			minStr = s.substring(i , j);
        		}
        	}
        	sourceHash[s.charAt(i)]--;
        }
        return minStr;
    }


	private boolean isValid(int[] targetHash, int[] sourceHash) {
		for(int i = 0; i < 256; i++){
			if(targetHash[i] > sourceHash[i]){
				return false;
			}
		}
		return true;
	}
}
