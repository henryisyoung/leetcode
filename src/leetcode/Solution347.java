package leetcode;
import java.util.*;
public class Solution347 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution347 t = new Solution347();
		int[] nums = {1,1,1,2,2,3,3,3};
		System.out.println(t.topKFrequent(nums, 2));
	}
	
    public List<Integer> topKFrequent(int[] nums, int k) {
    	List<Integer> rlt = new ArrayList<Integer>();
    	Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    	for(int i = 0; i < nums.length; i++){
    		if(map.containsKey(nums[i])){
    			map.put(nums[i], map.get(nums[i]) + 1);
    		}else{
    			map.put(nums[i], 1);
    		}
    	}
    	
    	while(k > 0){
    		int max = -1, num = -1;
    		for(int i : map.keySet()){
    			if(map.get(i) > max && !rlt.contains(i)){
    				max = map.get(i);
    				num = i;
    			}
    		}
    		rlt.add(num);
    		k--;
    	}
    	return rlt;
    }
    public List<Integer> topKFrequent2(int[] nums, int k) {
        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        for(int n : nums) {
            if(!map.containsKey(n)) map.put(n,0);
            int size = map.get(n);
            size++;
            map.put(n,size);
        }
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> i = set.iterator();
        PriorityQueue<Map.Entry<Integer, Integer>> queue = new PriorityQueue<>(set.size(),new myComparator());
        while(i.hasNext()) {
            queue.add(i.next());
        }
        while(k>0) {
            result.add(queue.poll().getKey());
            k--;
        }
        return result;
    }



    private class myComparator implements Comparator<Map.Entry<Integer, Integer>> {
        @Override
       public int compare(Map.Entry<Integer, Integer> p1, Map.Entry<Integer, Integer> p2) {
            if(p1.getValue() > p2.getValue()) return -1;
            else if(p1.getValue() == p2.getValue()) return 0;
            return 1;
        }
    }
}
