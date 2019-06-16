package leetcode.solution;
import java.util.*;
public class Solution77 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution77 t = new Solution77();
		System.out.println(t.combine(4, 2));
	}
    public List<List<Integer>> combine(int n, int k) {
    	if(n<0) return null;
    	List<Integer> list = new ArrayList<Integer>();
    	List<List<Integer>> ls = new ArrayList<List<Integer>>();
    	combineHelper(n,k,1,list,ls);
    	return ls;
    }
	private void combineHelper(int n, int k, int index ,List<Integer> list, List<List<Integer>> ls) {
		if(list.size()==k){
			ls.add(new ArrayList<Integer>(list)); // generate new list
			return;
		}else{
			
			for(int i=index;i<=n;i++){
				list.add(i);
			    combineHelper(n,k,i+1,list,ls);
			    list.remove(list.size()-1);
			}
		}
	}
}
