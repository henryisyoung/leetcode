package leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution15 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a ={-7,2,1,10,9,-10,-5,4,13,-9,-4,6,11,-12};
		         
		Solution15 t = new Solution15();
		
		
		System.out.println(t.threeSum(a));
		
	}
	
	public List<List<Integer>> threeSum(int[] nums){
		List<List<Integer>> lists = new ArrayList<List<Integer>>();
		Arrays.sort(nums);
		for(int i=0; i<nums.length-2;i++){
			int sum = -nums[i];
			int b = i+1, c=nums.length-1;
			while(b<c){
				if(sum==nums[b]+nums[c]){
					lists.add( Arrays.asList(nums[i],nums[b],nums[c]));
					do{ b++;}while(b<c && nums[b]==nums[b-1]);  
					do{ c--;}while(b<c && nums[c]==nums[c+1]);
				}else if(sum>nums[b]+nums[c]){
					b++;
				}else{
					c--;
				}
			}
			while( i<nums.length-2 && nums[i]==nums[i+1]){ i++;}
		}
		return lists;
	}

	private void BubbleSort(int[] nums) {
		// TODO Auto-generated method stub
		for(int i=nums.length-1;i>=0;i--){
			for(int j=1;j<=i;j++){
				if(nums[j-1]>nums[j]){
					int tmp = nums[j-1];
					nums[j-1] = nums[j];
					nums[j] = tmp;
				}
			}
		}
	}
	
    public static void mergeSort(int[] inputArray) {
        int size = inputArray.length;
        if (size < 2)
            return;
        int mid = size / 2;
        int leftSize = mid;
        int rightSize = size - mid;
        int[] left = new int[leftSize];
        int[] right = new int[rightSize];
        for (int i = 0; i < mid; i++) {
            left[i] = inputArray[i];

        }
        for (int i = mid; i < size; i++) {
            right[i - mid] = inputArray[i];
        }
        mergeSort(left);
        mergeSort(right);
        merge(left, right, inputArray);
    }

    public static void merge(int[] left, int[] right, int[] arr) {
        int leftSize = left.length;
        int rightSize = right.length;
        int i = 0, j = 0, k = 0;
        while (i < leftSize && j < rightSize) {
            if (left[i] <= right[j]) {
                arr[k] = left[i];
                i++;
                k++;
            } else {
                arr[k] = right[j];
                k++;
                j++;
            }
        }
        while (i < leftSize) {
            arr[k] = left[i];
            k++;
            i++;
        }
        while (j < leftSize) {
            arr[k] = right[j];
            k++;
            j++;
        }
    }

}
