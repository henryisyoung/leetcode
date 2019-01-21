package leetcode;
import java.util.*;
public class Solution60 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution60 t = new Solution60();
		System.out.println(t.getPermutation(8, 8590));
	}
	int time=0;
    public String getPermutation(int n, int k) {
        if(n<2) return Integer.toString(n);
        int sum=0,count=1;
        while(count<=n){
        	sum=10*sum+count;
        	count++;
        }
        //System.out.println(sum);
        char[] arr = Integer.toString(sum).toCharArray();
        List<String> list = new ArrayList<String>();
        getPermutationList(arr,0,list,k);
        Collections.sort(list);
        System.out.println(list.toString());
        if(k>=list.size()) return null;
        return list.get(k);
        
    }
	private void getPermutationList(char[] arr, int index, List<String> list,int k) {
		if(index==arr.length){
			list.add(new String(arr));
			return;
		}else{
			for(int i=index;i<arr.length;i++){
				swap(arr,i,index);
				getPermutationList(arr,index+1,list,k);
				swap(arr,i,index);
			}
		}
	}
	private void swap(char[] arr, int i, int j) {
		char c = arr[j];
		arr[j] = arr[i];
		arr[i] = c;
	}
	
	public String getPermutationMath(int n, int k) {
		int nn = factorial(n);
		//System.out.println(nn);
		if(k>nn) return null;
		StringBuffer sb = new StringBuffer();
		int[] arr= new int[n];
		for(int i=0;i<n;i++) arr[i]=i+1;
		k--;
		for(int i=0;i<n;i++){
			nn=nn/(n-i);
			int id = k/nn;
			sb.append(arr[id]);
			shift(arr,id);
			k=k%nn;
		}
		return sb.toString();
	}
	private void shift(int[] arr, int id) {
		if(id==arr.length-1) return;
		for(int i=id;i<arr.length-1;i++){
			arr[i]=arr[i+1];
		}
	}
	private int factorial(int n) {
		int count=1;
		for(int i=1;i<n+1;i++){
			count=count*i;
		}
		return count;
	}
	
	
	public String getPermutation2(int n, int k) {
		if(n == 0){
			return "";
		}
		int[] rlt = new int[n];
		for(int i = 1; i <= n; i++){
			rlt[i - 1] = i;
		}
		while(k > 1){
			nextPermutation(rlt);
			k--;
		}
		StringBuilder sb = new StringBuilder();
		for(int i : rlt){
			sb.append(i);
		}
		return sb.toString();
	}
	
	private void nextPermutation(int[] A) {
	    if(A == null || A.length <= 1) return;
	    int i = A.length - 2;
	    while(i >= 0 && A[i] >= A[i + 1]) i--; // Find 1st id i that breaks descending order
	    if(i >= 0) {                           // If not entirely descending
	        int j = A.length - 1;              // Start from the end
	        while(A[j] <= A[i]) j--;           // Find rightmost first larger id j
	        swap(A, i, j);                     // Switch i and j
	    }
	    Arrays.sort(A, i + 1, A.length);       // Reverse the descending sequence
	}

	private void swap(int[] A, int i, int j) {
	    int tmp = A[i];
	    A[i] = A[j];
	    A[j] = tmp;
	}
}
