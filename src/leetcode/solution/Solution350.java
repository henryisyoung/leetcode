package leetcode.solution;

import java.util.*;

public class Solution350 {
	public int[] intersect(int[] nums1, int[] nums2) {// from here
	    Arrays.sort(nums1);
	    Arrays.sort(nums2);
	    int section[]=new int [nums1.length];
	    int index1=0,index2=0,i=0;
	    for(index1=0,index2=0;(index1<nums1.length)&& (index2<nums2.length);){
	        if(nums1[index1]==nums2[index2]){
	            section[i++]=nums1[index1];
	            index1++;
	            index2++;
	        }
	        else if(nums1[index1]<nums2[index2]){
	            index1++;
	        }
	        else{
	            index2++;
	        }
	    }
	    int result[]=new int[i];
	    System.arraycopy(section, 0, result, 0, i);
	    return result;
	}
}
