package leetcode.solution;

import java.util.Arrays;
import java.util.Comparator;

public class Solution179 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public String largestNumber(int[] nums) {
    	
        if(nums == null || nums.length == 0){
        	return "";
        }
        String[] arr = new String[nums.length];
        for(int i = 0; i < nums.length; i++){
        	arr[i] = Integer.toString(nums[i]);
        }
        Comparator<String> comp = new Comparator<String>(){
        	public int compare(String s1, String s2){
        		return (s2 + s1).compareTo(s1 + s2);
        	}
        };
        Arrays.sort(arr, comp);
        StringBuilder sb = new StringBuilder();
        for(String str : arr){
        	sb.append(str);
        }
        String result = sb.toString();
        int index = 0;
        while (index < result.length() && result.charAt(index) == '0') {
            index++;
        }
        if (index == result.length()) {
            return "0";
        }
        return result;
    }
}
