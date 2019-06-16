package leetcode.solution;

import java.util.*;
import java.util.HashMap;
import java.util.List;

public class Solution17II {
	public List<String> letterCombinations(String digits) {
		List<String> result = new ArrayList<>();
		if (digits == null || digits.length() == 0) {
			return result;
		}
		dfsSearchAll(digits, 0, result, "");
		return result;
	}

	private void dfsSearchAll(String digits, int pos, List<String> result, String str) {
		if (pos == digits.length()) {
			result.add(str);
			return;
		}
		char digit = digits.charAt(pos);
		int count = digit == '9' || digit == '7' ? 4 : 3;
		int startIndex = ((digit - '0') - 2) * 3;
		startIndex = (digit - '0') > 7 ? startIndex + 1 : startIndex;
		for (int i = 0; i < count; i++) {
			char c = (char) ('a' + startIndex + i);
			dfsSearchAll(digits, pos + 1, result, str + c);
		}
	}
}
