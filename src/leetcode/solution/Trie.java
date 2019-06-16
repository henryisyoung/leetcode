package leetcode.solution;

public class Trie {
	class TrieNode {
	    private TrieNode[] children;
	    public boolean hasWord;
	    public TrieNode() {
	    	children = new TrieNode[26];
	    	hasWord = false;
	    }
	    
	    public void insert(String s, int index){
	    	if(index == s.length()){
	    		this.hasWord = true;
	    		return;
	    	}
	    	int pos = s.charAt(index) - 'a';
	    	if(children[pos] == null){
	    		children[pos] = new TrieNode();
	    	}
	    	children[pos].insert(s, index + 1);
	    }
	    
	    public TrieNode find(String s, int index){
	    	if(index == s.length()){
	    		return this;
	    	}
	    	int pos = s.charAt(index) - 'a';
	    	if(children[pos] == null){
	    		return null;
	    	}
	    	return children[pos].find(s, index + 1);
	    }
	}
	
	private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie.
    public void insert(String word) {
        root.insert(word, 0);
    }

    // Returns if the word is in the trie.
    public boolean search(String word) {
        TrieNode node = root.find(word, 0);
        return (node != null && node.hasWord);
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public boolean startsWith(String prefix) {
    	TrieNode node = root.find(prefix, 0);
    	return node != null;
    }
}
