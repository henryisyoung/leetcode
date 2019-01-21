package leetcode;
import java.util.*;

public class Solution140 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution140 t = new Solution140();
		Set<String> wordDict = new HashSet<String>();
		String[] a = {"cat", "cats", "and", "sand", "dog"};
		for(String str: a){
			wordDict.add(str);
		}
		
		System.out.println(t.wordBreak("catsanddog", wordDict));
	}
    public List<String> wordBreak(String s, Set<String> wordDict) {
    	List<String> rlt = new ArrayList<>();
        List<String> list = new ArrayList<String>();
        helper(rlt, list, 0, s, wordDict);
        return rlt;
    }
	private void helper(List<String> rlt, List<String> list, int count,
			String s, Set<String> set) {
		// TODO Auto-generated method stub
		if(count == s.length()){
			rlt.add(creatString(list));
			return;
		}
		for(int i = count + 1; i <= s.length(); i++){
			String str = s.substring(count, i);
			if(set.contains(str)){
				list.add(str);
				helper(rlt, list, count+str.length(), s, set);
				list.remove(list.size() - 1);
			}
		}
	}
	private String creatString(List<String> list) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < list.size(); i++){
			if(i < list.size() - 1){
				sb.append(list.get(i));
				sb.append(" ");
			}else{
				sb.append(list.get(i));
			}
		}
		return sb.toString();
	}
}
