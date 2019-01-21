package leetcode;
import java.util.*;

public class Question133_Graph {
      class UndirectedGraphNode {
          int label;
          List<UndirectedGraphNode> neighbors;
          UndirectedGraphNode(int x) { label = x; neighbors = new ArrayList<UndirectedGraphNode>(); }
      };

    public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
        if(node == null) return null;

        Map<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
        List<UndirectedGraphNode> list = new ArrayList<>();
        list.add(node);
        int index = 0;
        map.put(node, new UndirectedGraphNode(node.label));
        while (index < list.size()){
            UndirectedGraphNode cur = list.get(index++);
            for(UndirectedGraphNode n : cur.neighbors){
                if(!map.containsKey(n)){
                    map.put(n, new UndirectedGraphNode(n.label));
                    list.add(n);
                }
            }
        }
        for(int i = 0; i < list.size(); i++){
            UndirectedGraphNode cur = list.get(i);
            UndirectedGraphNode copy = map.get(cur);
            for(UndirectedGraphNode n : cur.neighbors){
                copy.neighbors.add(map.get(n));
            }
        }
        return map.get(node);
    }
    public UndirectedGraphNode cloneGraphWithQueue(UndirectedGraphNode node) {
        if(node == null) return null;
        Map<UndirectedGraphNode, UndirectedGraphNode> map = new HashMap<>();
        map.put(node, new UndirectedGraphNode(node.label));
        Queue<UndirectedGraphNode> queue = new LinkedList<>();
        queue.offer(node);
        while (!queue.isEmpty()){
            UndirectedGraphNode cur = queue.poll();
            UndirectedGraphNode copy = map.get(cur);
            for (UndirectedGraphNode n : cur.neighbors){
                if(map.containsKey(n)){
                    copy.neighbors.add(map.get(n));
                }else {
                    UndirectedGraphNode ncopy = new UndirectedGraphNode(n.label);
                    map.put(n,ncopy);
                    copy.neighbors.add(ncopy);
                    queue.add(n);
                }
            }
        }
        return map.get(node);
    }
}
