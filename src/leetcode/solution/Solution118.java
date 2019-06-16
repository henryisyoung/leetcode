package leetcode.solution;
import java.util.*;
public class Solution118 {

	public static void main(String[] args) {
		Solution118 t = new Solution118();
		System.out.println(t.generate(5));

	}
    public List<List<Integer>> generate(int numRows) {
    	
    	List<List<Integer>> rlt = new ArrayList<>();
        if(numRows == 0){
        	return rlt;
        }
        List<Integer> l = new ArrayList<Integer>();
        l.add(1);
        rlt.add(l);
        for(int i = 1; i < numRows; i++){
        	List<Integer> list = new ArrayList<Integer>();
        	for(int j = 0; j <= i; j++){
        		if(j == 0 || j == i){
        			list.add(1);
        		}else{
        			list.add(rlt.get(i - 1).get(j) + rlt.get(i - 1).get(j - 1));
        		}
        	}
        	rlt.add(list);
        }
        return rlt;
    }
}
