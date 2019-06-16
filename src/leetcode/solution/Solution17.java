package leetcode.solution;

import java.util.ArrayList;
import java.util.List;

public class Solution17 {

	private void helper(String digits, int index, String result, List<String> results)
	{
	    if (index >= digits.length())
	    {
	        results.add(result);
	    }
	    else
	    {
	        char digit = digits.charAt(index);
	        int count = digit == '9' || digit == '7' ? 4 : 3;
	        int startIndex = ((digit - '0') - 2) * 3;
	        startIndex = (digit - '0') > 7 ? startIndex + 1 : startIndex;
	        for (int i = 0; i < count; i++)
	        {
	            helper(digits, index + 1, result + (char)((startIndex + i) + 'a'), results);
	        }
	    }
	}

	public List<String> letterCombinations(String digits) {
	    List<String> results = new ArrayList<String>();
	    if (digits.isEmpty())
	    {
	        return results;
	    }
	    helper(digits, 0, "", results);
	    return results;
	}
}
