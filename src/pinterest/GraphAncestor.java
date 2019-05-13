package pinterest;

import java.util.*;

public class GraphAncestor {
    Map<Integer, List<Integer>> ancestorsMap;
    public List<Integer> findNodes(int[][] nums) {
        List<Integer> result = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return result;
        }
        Map<Integer, Integer> inDegree = new HashMap<>();
        this.ancestorsMap = new HashMap<>();
        for (int [] arr : nums ) {
            int from = arr[0], to = arr[1];
            if (!inDegree.containsKey(from)) {
                inDegree.put(from, 0);
                ancestorsMap.put(from, new ArrayList<>());
            }
            if (!inDegree.containsKey(to)) {
                inDegree.put(to, 1);
                ancestorsMap.put(to, new ArrayList<>());
            } else {
                inDegree.put(to, inDegree.get(to) + 1);
            }

            ancestorsMap.get(to).add(from);
        }

        for (int key : inDegree.keySet()) {
            if (inDegree.get(key) == 0) {
                result.add(key);
            }
        }

        return result;
    }

    public int findTwoNodesAncestor(int a, int b) {
        List<Integer> ancestorsSet = new ArrayList<>();
        findNodeAllAncestors(a, ancestorsSet);
        List<Integer> bFathers = ancestorsMap.get(b);
        Queue<Integer> queue = new LinkedList<>();
        queue.addAll(bFathers);

        System.out.println("a ancestors " + ancestorsSet.toString());
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (ancestorsSet.contains(cur)) {
                return cur;
            }
            if (ancestorsMap.containsKey(cur)) {
                for (int curFather : ancestorsMap.get(cur)) {
                    queue.add(curFather);
                }
            }
        }

        return -1;
    }

    private void findNodeAllAncestors(int i, List<Integer> ancestorsSet) {
        if (!ancestorsMap.containsKey(i) || ancestorsMap.get(i).size() == 0) {
            return;
        }
        for (int father : ancestorsMap.get(i)) {
            ancestorsSet.add(father);
            findNodeAllAncestors(father, ancestorsSet);
        }
    }

    public int furthestAncestor(int a) {
        if (ancestorsMap.get(a).size() == 0) {
            return a;
        }
        Queue<Integer> queue = new LinkedList<>();
        queue.add(a);
        int result = a;
        while (!queue.isEmpty()) {
            int size = queue.size();
            result = queue.peek();
            for (int i = 0; i < size; i++) {
                int cur = queue.poll();
                for (int curFather : ancestorsMap.get(cur)) {
                    queue.add(curFather);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] nums = {{1,4},{2,4},{4,7},{3,5},{5,7},{5,8},{3,6},{6,9}}; // ,{0,3}

        GraphAncestor sovler = new GraphAncestor();
        List<Integer> result = sovler.findNodes(nums);
        System.out.println(result.toString());

        System.out.println(sovler.findTwoNodesAncestor(7,8));
//        System.out.println(sovler.findTwoNodesAncestor(1,4)); //-1

        System.out.println(sovler.furthestAncestor(9));
    }
}
