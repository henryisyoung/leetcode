package pinterest;

import java.util.*;

public class BehaviorGraph {
    class TrieNode {
        TrieNode[] children;
        boolean isWord;
        int count;
        public TrieNode() {
            this.count = 0;
            this.isWord = false;
            this.children = new TrieNode[26];
        }
    }
    class Trie {
        TrieNode root;

        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(List<String> list) {
//            System.out.println(list.toString());
            TrieNode node = root;
            for (int i = 0; i < list.size(); i++) {
                int pos = list.get(i).charAt(0) - 'A';
//                System.out.println(pos);
                if (node.children[pos] == null) {
                    node.children[pos] = new TrieNode();
                }
                node = node.children[pos];
                node.count++;
            }
            node.isWord = true;
        }

    }

    public void behaviorGraph(String[] logs) {
        Map<Integer, List<String>> map = new HashMap<>();
        for (String log : logs) {
            String[] arr = log.split(",");
            int id = Integer.parseInt(arr[0]);
            String action = arr[2];
            if (!map.containsKey(id)) {
                map.put(id, new ArrayList<>());
            }
            map.get(id).add(action);
        }
        Trie trie = new Trie();
        for (int id : map.keySet()) {
            trie.insert(map.get(id));
        }
        draw(trie.root);
    }

    private void draw(TrieNode root) {
        if (root.isWord) {
            return;
        }
        for (int i = 0; i < 26; i++) {
            if (root.children[i] != null) {
                System.out.println((char) (i + 'A') );
                draw(root.children[i]);
            }
        }
    }

    public static void main(String[] args) {
//        user_id, timestamp, action
//        100, 1000, A
//        200, 1003, A
//        300, 1009, B
//        100, 1026, B
//        100, 1030, C
//        200, 1109, B
//        200, 1503, A
        String[] logs= {"100,1000,A","200,1003,A","300,1009,B",
                "100,1026,B","100,1030,C","200,1109,B","200,1503,A"};
        BehaviorGraph sovler = new BehaviorGraph();
        sovler.behaviorGraph(logs);
    }
}
