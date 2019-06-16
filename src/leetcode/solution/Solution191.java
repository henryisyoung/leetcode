package leetcode.solution;

public class Solution191 {
    public int hammingWeight(int n) {
        if (n == 0) {
            return 0;
        }
        
        int count = 1;
        while ((n & (n - 1)) != 0) {
            n &= n-1;
            count++;
        }
        return count;
    }
    public int hammingWeight2(int n) {
        int count = 0;
        for(int i=1; i<33; i++){
            if(getBit(n, i) == true){
                count++;
            }
        }
        return count;
    }
     
    public boolean getBit(int n, int i){
        return (n & (1 << i)) != 0;
    }
    
}
