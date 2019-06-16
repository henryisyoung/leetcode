package leetcode.solution;

import java.util.ArrayList;
import java.util.List;


public class Solution22II {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solution22II t = new Solution22II();
		System.out.println(t.generateParenthesis(3));
	}

	public List<String> generateParenthesis(int n) {
		List<String> result = new ArrayList<>();
		if (n == 0) {
		    return result;
        }
        dfsSearchAll(n, result, "", 0, 0);
        return result;
	}

    private void dfsSearchAll(int n, List<String> result, String str, int left, int right) {
	    if (left == n && right == n) {
	        result.add(str);
	        return;
        }
        if (left < n) {
            dfsSearchAll(n, result, str + '(', left + 1, right);
        }
        if (right < left) {
	        dfsSearchAll(n, result, str + ')', left, right + 1);
        }
    }
}
