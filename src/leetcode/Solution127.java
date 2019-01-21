package leetcode;
import java.util.*;
public class Solution127 {
	public static void main(String[] args) {
		String beginWord = "hot", endWord = "dog";
		Set<String> wordList = new HashSet<String>();
		wordList.add("hot");
		wordList.add("dot");
		wordList.add("dog");
		
		Solution127 t = new Solution127();
		System.out.println(t.ladderLength(beginWord, endWord, wordList));
		//System.out.println(t.neighbors(endWord, wordList));
	}
	
    public int ladderLength(String beginWord, String endWord, Set<String> wordList) {
        if(beginWord.equals(endWord)) return 1;
        if(wordList == null) return 0;
        
        int length = 1;
        HashSet<String> set = new HashSet<String>();
        Queue<String> queue = new LinkedList<String>();
        
        wordList.add(beginWord);
        wordList.add(endWord);
        
        set.add(beginWord);
        queue.offer(beginWord);
        
        while(!queue.isEmpty()){
        	int size = queue.size();
        	length++;
        	for(int i = 0; i < size; i++){
        		String head = queue.poll();
        		for(String str : neighbors(head, wordList)){
        			if(set.contains(str)){
        				continue;
        			}
        			if(str.equals(endWord)){
        				return length;
        			}
        			set.add(str);
        			queue.offer(str);
        		}
        	}
        }
        
        return 0;
    }

    public ArrayList<String> neighbors(String head, Set<String> dict) {
		ArrayList<String> rlt = new ArrayList<String>();
		for(char c = 'a'; c <= 'z'; c++){
			for(int j = 0; j < head.length();j++){
				if(c != head.charAt(j)){
					if(dict.contains(replace(head,j,c))){
						rlt.add(replace(head,j,c));
					}
				}
			}
		}
		return rlt;
	}

    public String replace(String head, int j, char c) {
		char[] arr = head.toCharArray();
		arr[j] = c;
		return new String(arr);
	}
}
