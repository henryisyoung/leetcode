package leetcode;
import java.util.*;
public class Solution349 {
    public int[] intersection(int[] nums1, int[] nums2) {
    	if(nums1 == null || nums2 == null) return null;
    	if(nums1.length == 0 || nums2.length == 0) return new int[0];
        HashSet<Integer> set = new HashSet<Integer>();
        HashSet<Integer> set2 = new HashSet<Integer>();
        for(int i : nums1) set.add(i);
        for(int i : nums2){
        	if(set.contains(i)){
        		set2.add(i);
        	}
        }
        int[] rlt = new int[set2.size()];
        int index = 0;
        for(int i : set2){
        	rlt[index++] = i;
        }
        return rlt;
    }
}
