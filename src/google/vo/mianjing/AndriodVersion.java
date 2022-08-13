package google.vo.mianjing;

import java.util.*;

public class AndriodVersion {
    static class Node {
        int  pos;
        boolean isStart;
        String version;
        public Node(int pos, String version, boolean isStart) {
            this.pos = pos;
            this.version = version;
            this.isStart = isStart;
        }

    }
    public static List<List<String>> AndriodVersion (String[] versions, int[][] intervals) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < versions.length; i++) {
            nodes.add(new Node(intervals[i][0], versions[i], true));
            nodes.add(new Node(intervals[i][1], versions[i], false));
        }

        Collections.sort(nodes, (a, b) -> {
            if (a.pos != b.pos) {
                return a.pos - b.pos;
            }
            if (a.isStart) {
                if (!b.isStart) {
                    return 1;
                }
                return 0;
            }
            return -1;
        });
        Set<String> set = new HashSet<>();
        Integer prev = null;
        List<List<String>> result = new ArrayList<>();
        boolean lastIsEnd = false;
        for (Node node : nodes) {
            if (node.isStart) {
                if (!set.isEmpty()) {
                    if (prev != null && node.pos != prev) {
                        String interval = prev + "-" + (node.pos - 1);
                        String curVersions = set.toString();
                        List<String> curList = Arrays.asList(interval, curVersions);
                        result.add(curList);
                    }
                }
                set.add(node.version);
                prev = node.pos;
                lastIsEnd = false;
            } else {
                if (node.pos != prev) {
                    String interval = lastIsEnd ? (prev + 1 )+ "-" + (node.pos) : prev+ "-" + (node.pos);
                    String curVersions = set.toString();
                    List<String> curList = Arrays.asList(interval, curVersions);
                    result.add(curList);
                }
                set.remove(node.version);
                prev = node.pos;
                lastIsEnd = true;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String[] versions = {"v1", "v2"};
        int[][] intervals = {
                {1,6},
                {3,7},
        };
        System.out.println(AndriodVersion(versions, intervals));

        int[][] intervals2 = {
                {1,6},
                {3,4},
        };
        System.out.println(AndriodVersion(versions, intervals2));

        String[] versions3 = {"v1", "v2", "v3"};
        int[][] intervals3 = {
                {1,6},
                {3,4},
                {4,7},
        };
        System.out.println(AndriodVersion(versions3, intervals3));

    }
}
