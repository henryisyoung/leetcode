package leetcode;

public class Solution28 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution28 t = new Solution28();
		System.out.println(t.strStr("", ""));

	}
	
	public int strStr(String haystack, String needle) {
		if(haystack=="" || needle=="") return -1;
		int len=needle.length();
		for(int i=0;i<haystack.length()-len+1;i++){
			if(haystack.charAt(i)==needle.charAt(0)){
				if(haystack.substring(i,i+len).equals(needle))
					return i;
			}
		}
        return -1;
    }

}
