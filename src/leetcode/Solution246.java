package leetcode;

import java.util.HashMap;

public class Solution246 {

	public static void main(String[] args) {
		Solution246 t = new Solution246();
		System.out.println(t.isStrobogrammatic("  "));

	}
    public boolean isStrobogrammatic(String num) {
        HashMap<Character, Character> map = new HashMap<Character, Character>();
        map.put('1','1');
        map.put('0','0');
        map.put('6','9');
        map.put('9','6');
        map.put('8','8');
        int left = 0, right = num.length() - 1;
        while(left <= right){
            char c1 = num.charAt(left), c2 = num.charAt(right);
            if(!map.containsKey(c1) || c2 != map.get(c1)){
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
