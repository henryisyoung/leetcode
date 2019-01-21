package interview;

import java.util.*;

public class longestCommonSubstrings {
//http://stackoverflow.com/questions/34805488/finding-all-the-common-substrings-of-given-two-strings
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		longestCommonSubstrings t = new longestCommonSubstrings();
		System.out.println(t.lcs("yellow ", "hellow"));

	}
	public static Set<String> lcs(String s, String t) {
	    int[][] table = new int[s.length()][t.length()];
	    int longest = 0;
	    Set<String> result = new HashSet<>();

	    for (int i = 0; i < s.length(); i++) {
	        for (int j = 0; j < t.length(); j++) {
	            if (s.charAt(i) != t.charAt(j)) {
	                continue;
	            }

	            table[i][j] = (i == 0 || j == 0) ? 1
	                                             : 1 + table[i - 1][j - 1];
	            if (table[i][j] > longest) {
	                longest = table[i][j];
	                result.clear();
	            }
	            if (table[i][j] == longest) {
	                result.add(s.substring(i - longest + 1, i + 1));
	            }
	        }
	    }
	    return result;
	}
}
