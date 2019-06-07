package leetcode.dynamicProgramming;

public class CountingBits {
    public int[] countBits(int num) {
        int[] result = new int[num+1];

        int p = 1; //p tracks the index for number x
        for(int i = 1; i <= num; i++){
            if((i & (i-1))==0){
                result[i] = 1;
                p = 1;
            }else{
                result[i] = result[p] + 1;
                p++;
            }
        }
        return result;
    }
}
