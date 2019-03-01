package airbnb;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.*;

public class Pagination {
    public List<String> displayPages(List<String> input, int pageSize) {
        List<String> result = new ArrayList<>();
        if (input == null || input.size() == 0 || pageSize == 0) {
            return result;
        }
        Iterator<String> iter = input.iterator();
        List<String> isVisited = new ArrayList<>();
        boolean end = false;
        while (iter.hasNext()) {
            String cur = iter.next();
            String hostID = cur.split(",")[0];
            if (!isVisited.contains(hostID) || end) {
                result.add(cur);
                isVisited.add(hostID);
                iter.remove();
            }
            if (isVisited.size() == pageSize) {
                isVisited.clear();
                end = false;
                if (!input.isEmpty()) {
                    result.add(" ");
                }
                iter = input.iterator();
            }
            if (!iter.hasNext()) {
                iter = input.iterator();
                end = true;
            }
        }
        return result;
    }

    public static List<String> displayPages2(List<String> input, int pageSize) {
        List<String> result = new ArrayList<>(); //把hostID相同的归类到一个List里面
        Map<String, List<String>> map = new HashMap<>();
        for (String record : input) {
            String hostId = record.split(",")[0];
            if (!map.containsKey(hostId)) {
                map.put(hostId, new ArrayList<>());
            }
            map.get(hostId).add(record);
        }

        Queue<Node> queue = new LinkedList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String hostId = entry.getKey();
            List<String> list = entry.getValue();
            queue.offer(new Node(hostId, 0, list.get(0)));
        }
        int count = 0;
        List<Node> temp = new ArrayList<>();
        while (!queue.isEmpty()) {
            Node head = queue.poll();
            temp.add(head);
            count++;
            if (head.j + 1 != map.get(head.hostId).size()) {
                queue.offer(new Node(head.hostId, head.j + 1,
                        map.get(head.hostId).get(head.j + 1)));
            }
            if (count == pageSize) {
                copy(temp, result);
                count = 0;
            }
        }
        if (!temp.isEmpty()) {
            copy(temp, result);
        }
        return result;
    }

    private static void copy(List<Node> temp, List<String> result) {
        //as map key doesn't has order so we need to reorder each page
        Collections.sort(temp, new Comparator<Node>(){
            public int compare (Node n1, Node n2) {
                return (int) (Double.parseDouble(n2.s.split(",")[2]) -
                        Double.parseDouble(n1.s.split(",")[2]));
            }
        });
        for (Node node : temp) {
            result.add(node.s);
        }
        result.add(" ");
        temp.clear();
    }

    private static class Node {
        String hostId;
        List<String> list;
        public String s;
        public int j;

        public Node(String hostId, int pos, String s) {
            this.hostId = hostId;
            this.s = s;
            this.j = pos;
        }
    }

    /// /O(n)时间复杂度的solution
    public static List<String> displayPages3(List<String> input, int pageSize) {
        List<String> result = new ArrayList<>();
        Map<String, Set<String>> map = new HashMap<>();
        for (String record : input) {
            String hostId = record.split(",")[0];
            if (!map.containsKey(hostId)) {
                map.put(hostId, new LinkedHashSet<>());
            }
            map.get(hostId).add(record);
        }
        Set<String> set = new LinkedHashSet<>();
        for (String record : input) {
            set.add(record);
        }
        int count = 0;
        while (!set.isEmpty()) {
            //第一页不加分页符“”，其他页都要加
            if (result.size() != 0) {
                result.add(" ");
            }
            //先从分号类的里面去加
            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                if (entry.getValue().size() == 0) continue;
                if (count < pageSize) {
                    String record = entry.getValue().iterator().next();
                    result.add(record);
                    count++;
                    sync(set, map, record);
                } else {
                    break;
                }
            }
            Iterator<String> it = set.iterator();
            while (count < pageSize && it.hasNext()) {
                String record = it.next();
                result.add(record);
                sync(set, map, record);
                count++;
            }
            count = 0;
        }
        return result;
    }

    private static void sync(Set<String> set, Map<String, Set<String>> map, String del) {
        String hostId = del.split(",")[0];
        set.remove(del);
        map.get(hostId).remove(del);
    }

    public static void main(String[] args) {
        List<String> input = new ArrayList<>();
        input.add("1,28,310.6,SF");
        input.add("4,5,204.1,SF");
        input.add("20,7,203.2,Oakland");
        input.add("6,8,202.2,SF");
        input.add("6,10,199.1,SF");
        input.add("1,16,190.4,SF");
        input.add("6,29,185.2,SF");
        input.add("7,20,180.1,SF");
        input.add("6,21,162.1,SF");
        input.add("2,18,161.2,SF");
        input.add("2,30,149.1,SF");
        input.add("3,76,146.2,SF");
        input.add("2,14,141.1,San Jose");
        List<String> result = displayPages2(input, 5);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
    }
}
