package leetcode.solution;

public class Solution161 {
	public static void main(String[] args) {
		Solution161 t = new Solution161();
		System.out.println(t.isOneEditDistance2("abc", "bas"));
	}
    public boolean isOneEditDistance(String s, String t) {
        int m = s.length(), n = t.length();
        if(m == n) return isOneModified(s, t);
        if(m - n == 1) return isOneDeleted(s, t);
        if(n - m == 1) return isOneDeleted(t, s);
        // 长度差距大于2直接返回false
        return false;
    }
    
    private boolean isOneModified(String s, String t){
        boolean modified = false;
        // 看是否只修改了一个字符
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) != t.charAt(i)){
                if(modified) return false;
                modified = true;
            }
        }
        return modified;
    }
    
    public boolean isOneDeleted(String longer, String shorter){
        // 找到第一组不一样的字符，看后面是否一样
        for(int i = 0; i < shorter.length(); i++){
            if(longer.charAt(i) != shorter.charAt(i)){
            	return longer.substring(i + 1).equals(shorter.substring(i));
            }
        }
        return true;
    }
    
    public boolean isOneEditDistance2(String s1, String s2) {
    	int m = s1.length(), n = s2.length();

    	// If difference between lengths is more than
    	// 1, then strings can't be at one distance
    	if (Math.abs(m - n) > 1)
    		return false;
    	int count = 0; // Count of edits
    	int i = 0, j = 0;
    	while (i < m && j < n)
    	{
    		// If current characters don't match
    		if (s1.charAt(i) != s2.charAt(j))
    		{
    			if (count == 1)
    				return false;
    			// If length of one string is
    			// more, then only possible edit
    			// is to remove a character
    			if (m > n)
    				i++;
    			else if (m< n)
    				j++;
    			else //Iflengths of both strings is same
    			{
    				i++;
    				j++;
    			}
    			// Increment count of edits 
    			count++;
    		}
    		else // If current characters match
    		{
    			i++;
    			j++;
    		}
    	}
    	// If last character is extra in any string
    	if (i < m || j < n)
    		count++;
    	return count == 1;
    }
}
