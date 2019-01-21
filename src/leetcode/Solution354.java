package leetcode;

import java.util.*;;

public class Solution354 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution354 t = new Solution354();
		int[][] a =  {{2,4},{2,3},{3,6}};
		Arrays.sort(a, new Comparator<int[]>(){
			@Override
			public int compare(int[] a, int[] b) {
				if(a[0] == b[0]) return a[1] - b[1];
				return a[0] - b[0];
			}
		});
		for(int[] i : a){
			System.out.println(Arrays.toString(i));
		}
	}
	
    public int maxEnvelopes(int[][] envelopes) {
        if(envelopes == null || envelopes.length == 0) return 0;
        if(envelopes[0] == null || envelopes[0].length == 0) return 0;
        
        quickSort(0, envelopes.length - 1, envelopes);
        int rlt = findIncreaseing(envelopes);
        return rlt;
    }

	private int findIncreaseing(int[][] envelopes) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void quickSort(int start, int end, int[][] envelopes) {
		if(start >= end) return;
		
		int left = start, right = end, mid = left + (end - start)/2, 
				pivot = envelopes[mid][0];
		
		while(left <= right){
			while(envelopes[left][0] < pivot){
				left++;
			}
			while(envelopes[right][0] > pivot){
				right--;
			}
			if(left <= right){
				int tmp = envelopes[left][0];
				envelopes[left][0] = envelopes[right][0];
				envelopes[right][0] = tmp;
			}
		}
		if(left < end) quickSort(left, end, envelopes);
		if(right > start) quickSort(start, right, envelopes);
	}
}
