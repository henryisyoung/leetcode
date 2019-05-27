package google;

import java.util.*;

public class FindAndReplaceInString {
    class Node {
        int index;
        String source, target;
        public Node(int index, String source, String target) {
            this.target = target;
            this.source = source;
            this.index = index;
        }
    }
    public String findReplaceString(String S, int[] indexes, String[] sources, String[] targets) {
        if (indexes == null || indexes.length == 0) {
            return S;
        }
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < indexes.length; i++) {
            nodes.add(new Node(indexes[i], sources[i], targets[i]));
        }
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.index - o1.index;
            }
        });
        for (Node n : nodes) {
            int index = n.index;
            String source = n.source;
            String target = n.target;
            if (S.substring(index, index + source.length()).equals(source)) {
                S = S.substring(0, index) + target + S.substring(index + source.length());
            }
        }
        return S;
    }
    public String findReplaceString2(String S, int[] indexes, String[] sources, String[] targets) {
        if (indexes == null || indexes.length == 0) {
            return S;
        }
        StringBuilder sb = new StringBuilder();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < indexes.length; i++) {
            if (S.substring(indexes[i], indexes[i] + sources[i].length()).equals(sources[i])) {
                map.put(indexes[i], i);
            }
        }
        for (int i = 0; i < S.length(); i++) {
            if (map.containsKey(i)) {
                int pos = map.get(i);
                sb.append(targets[pos]);
                i += sources[pos].length() - 1;
            } else {
                sb.append(S.charAt(i));
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        String S = "abcd";
        int[] indexes = {0,2};
        String[] sources = {"ab","ec"}, targets = {"eee","ffff"};
        FindAndReplaceInString solver = new FindAndReplaceInString();
        System.out.println(solver.findReplaceString2(S, indexes, sources, targets));
    }
}
