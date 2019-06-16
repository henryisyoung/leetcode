package leetcode.solution;
import java.util.*;
public class Solution71 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution71 t = new Solution71();
		System.out.println(t.simplifyPath("/"));
	}
    public String simplifyPath(String path) {
        if(path=="" || path==null) return path;
        StringBuilder sb = new StringBuilder();
        Stack<String> stack = new Stack<String>();
        String[] strarr = path.split("/");
        for(String str : strarr){
        	if(str.equals("") || str.length()==0) continue;
        	else if(str.equals("")){
        		if(! stack.isEmpty()){
        			stack.pop();
        		}
        	}else{
        		stack.push(str);
        	}
        }
        if(stack.isEmpty()) return "/";
        
        Stack<String> ns = new Stack<String>();
        while(! stack.isEmpty()){
        	ns.push(stack.pop());
        }
        while(! ns.isEmpty()){
        	sb.append("/" + ns.pop());
        }
        return sb.toString();
    }
}
