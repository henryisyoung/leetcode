package leetcode.solution;

public class Solution58 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Solution58 t = new Solution58();
		String s = "this is good";
		
		System.out.println(s.trim());
	}
    public int lengthOfLastWord(String s) {
        String[] arr = s.split(" ");
        if(arr.length<1) return 0;
        return arr[arr.length-1].length();
    }
    
    public int lengthOfLastWord2(String s) {
    	if(s.length()==0) return 0;
    	int len =s.length()-1,count=0;
    	for(int i=len;i>=0;i--){
    		if(s.charAt(i)!=' '){
    			count++;
    		}
    		if(count>0&&s.charAt(i)==' ')
    			return count;
    	}
    	return count;
    }

    public int lengthOfLastWord3(String s) {
        s = s.trim();
        if (s.equals(""))
            return 0;
        int res = 0;
        for (int i = s.length() - 1; i > -1 && s.charAt(i) != ' '; ++res, --i);
        return res;
    }
}
