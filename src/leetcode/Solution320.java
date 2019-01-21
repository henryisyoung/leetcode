package leetcode;
import java.util.*;
public class Solution320 {
	public List<String> generateAbbreviations(String word) {
        List<String> res = new ArrayList<>();  
        dfs(res, "", 0, word);  
        return res;  
    }  
      
    public void dfs(List<String> res, String curr, int start, String s) {  
        res.add(curr + s.substring(start));                     
        if (start == s.length())   
            return;  
                                                  
        // 定义新的起始位置  
        int i = 0;  
          
        // 除了最开始，起始位置都要与之前结尾位置隔一个  
        if (start > 0) {  
            i = start + 1;  
        }  
          
        for (; i < s.length(); i++) {  
            String prefix = curr + s.substring(start, i);                 
            // 以ith字符开头，依次替换j个字母成数字。  
            for (int j = 1; j <= s.length() - i; j++) {  
                dfs(res,  prefix+ j, i + j, s);  
                //i+j,表示从上一个start开始替换j个字母后开始的字母位置。  
            }  
        }  
    }  
}
