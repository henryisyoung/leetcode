package leetcode.solution;

public class Question344_Sorted_Array {
    public String reverseString(String s) {
        if(s == null || s.length() == 0) return s;
        char[] arr = s.toCharArray();
        int i = 0, j = arr.length - 1;
        while(i < j){
            char tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
            i++; j--;
        }
        return new String(arr);
    }
}
