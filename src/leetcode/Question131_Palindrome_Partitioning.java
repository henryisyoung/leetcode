package leetcode;

import java.util.ArrayList;
import java.util.List;

public class Question131_Palindrome_Partitioning {
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        List<String> list = new ArrayList<>();
        partitionHelper(list, result, s, 0);
        return result;
    }

    private void partitionHelper(List<String> list, List<List<String>> result, String s, int pos) {
        if(pos == s.length()){
            result.add(new ArrayList<String>(list));
            return;
        }
        for(int i = pos + 1; i <= s.length(); i++){
            String sub = s.substring(pos, i);
            if(!isValidPalindrome(sub)) continue;
            list.add(sub);
            partitionHelper(list, result, s, i);
            list.remove(list.size()-1);
        }
    }

    private boolean isValidPalindrome(String s) {
        int end = s.length() - 1;
        int start = 0;
        while (start < end){
            if(s.charAt(start) != s.charAt(end)){
                return false;
            }
            start++;
            end--;
        }
        return true;
    }
}
