package google.vo.mianjing;

import java.util.HashMap;
import java.util.Map;

public class DirectoryTrie {

    class Node {
        int size;
        String id;
        boolean isFile;
        Map<String, Node> children;
        public Node() {
            this.children = new HashMap<>();
            this.id = "";
            this.size = 0;
            this.isFile = false;
        }
    }
    Map<String, Node> nodesMap;
    Node root;
    public DirectoryTrie(String[][] dirs, Map<String, Integer> sizesMap) {
        this.nodesMap = new HashMap<>();
        this.root = new Node();
        buildTrie(dirs, sizesMap);
    }

    public int findNodeSize(String id) {
        if (!nodesMap.containsKey(id)) {
            return -1;
        }
        Node node = nodesMap.get(id);
        return dfsFindAllFiles(node);
    }

    private int dfsFindAllFiles(Node node) {
        if (node.isFile) {
            return node.size;
        }
        int sum = 0;
        for (Node next : node.children.values()) {
            sum += dfsFindAllFiles(next);
        }
        return sum;
    }

    private void buildTrie(String[][] dirs, Map<String, Integer> sizesMap) {
        for (String[] dir : dirs) {
            insert(dir, sizesMap);
        }
    }

    private void insert(String[] dir, Map<String, Integer> sizesMap) {
//        Node node = root;
//        dfsInsert(0, dir, node, si)
    }
}
