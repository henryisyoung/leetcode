package leetcode.solution;

public class Solution134 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
    public int canCompleteCircuit(int[] gas, int[] cost) {
        if(gas == null || cost == null || gas.length != cost.length){
        	return -1;
        }
        int n = gas.length, start = 0, sum = 0, total = 0;
        for(int i = 0; i < n; i++){
        	int diff = gas[i] - cost[i];
        	sum += diff;
        	total += diff;
        	if(sum < 0){
        		start = i + 1;
        		sum = 0;
        	}
        }
        if(total < 0){
        	return -1;
        }
        return start;
    }
}
