package leetcode.solution;
import java.util.*;
public class Solution266 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution266 t = new Solution266();
		String s = "aaa";
		System.out.println(t.canPermutePalindrome(s));
	}
    public boolean canPermutePalindrome(String s) {
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        // 统计每个字符的个数
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            Integer cnt = map.get(c);
            if(cnt == null){
                cnt = new Integer(0);
            }
            map.put(c, ++cnt);
        }
        // 判断是否只有不大于一个的奇数次字符
        boolean hasOdd = false;
        for(Character c : map.keySet()){
            if(map.get(c) % 2 == 1){
                if(!hasOdd){
                    hasOdd = true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
