package leetcode;
import java.util.*;
public class Solution269 {
	 public String alienOrder(String[] words) {
	        // 节点构成的图
	        Map<Character, Set<Character>> graph = new HashMap<Character, Set<Character>>();
	        // 节点的计数器
	        Map<Character, Integer> indegree = new HashMap<Character, Integer>();
	        // 结果存在这个里面
	        StringBuilder order = new StringBuilder();
	        // 初始化图和计数器
	        initialize(words, graph, indegree);
	        // 建图并计数
	        buildGraphAndGetIndegree(words, graph, indegree);
	        // 拓扑排序的最后一步，根据计数器值广度优先搜索
	        topologicalSort(order, graph, indegree);
	        // 如果大小相等说明无环
	        return order.length() == indegree.size() ? order.toString() : "";
	    }
	    
	    private void initialize(String[] words, Map<Character, Set<Character>> graph, Map<Character, Integer> indegree){
	        for(String word : words){
	            for(int i = 0; i < word.length(); i++){
	                char curr = word.charAt(i);
	                // 对每个单词的每个字母初始化计数器和图节点
	                if(graph.get(curr) == null){
	                    graph.put(curr, new HashSet<Character>());
	                }
	                if(indegree.get(curr) == null){
	                    indegree.put(curr, 0);
	                }
	            }
	        }
	    }
	    
	    private void buildGraphAndGetIndegree(String[] words, Map<Character, Set<Character>> graph, Map<Character, Integer> indegree){
	        Set<String> edges = new HashSet<String>();
	        for(int i = 0; i < words.length - 1; i++){
	        // 每两个相邻的词进行比较
	            String word1 = words[i];
	            String word2 = words[i + 1];
	            for(int j = 0; j < word1.length() && j < word2.length(); j++){
	                char from = word1.charAt(j);
	                char to = word2.charAt(j);
	                // 如果相同则继续，找到两个单词第一个不相同的字母
	                if(from == to) continue;
	                // 如果这两个字母构成的边还没有使用过，则
	                if(!edges.contains(from+""+to)){
	                    Set<Character> set = graph.get(from);
	                    set.add(to);
	                    // 将后面的字母加入前面字母的Set中
	                    graph.put(from, set);
	                    Integer toin = indegree.get(to);
	                    toin++;
	                    // 更新后面字母的计数器，+1
	                    indegree.put(to, toin);
	                    // 记录这条边已经处理过了
	                    edges.add(from+""+to);
	                    break;
	                }
	            }
	        }
	    }
	    
	    private void topologicalSort(StringBuilder order, Map<Character, Set<Character>> graph, Map<Character, Integer> indegree){
	        // 广度优先搜索的队列
	        Queue<Character> queue = new LinkedList<Character>();
	        // 将有向图的根，即计数器为0的节点加入队列中
	        for(Character key : indegree.keySet()){
	            if(indegree.get(key) == 0){
	                queue.offer(key);
	            }
	        }
	        // 搜索
	        while(!queue.isEmpty()){
	            Character curr = queue.poll();
	            // 将队头节点加入结果中
	            order.append(curr);
	            Set<Character> set = graph.get(curr);
	            if(set != null){
	                // 对所有该节点指向的节点，更新其计数器，-1
	                for(Character c : set){
	                    Integer val = indegree.get(c);
	                    val--;
	                    // 如果计数器归零，则加入队列中待处理
	                    if(val == 0){
	                        queue.offer(c);
	                    }
	                    indegree.put(c, val);
	                }
	            }
	        }
	    }
}
