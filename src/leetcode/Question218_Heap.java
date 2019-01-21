package leetcode;

import java.util.*;

public class Question218_Heap {
    private class Edge{
        int x;
        int h;
        boolean isStart;
        public Edge (int x, int h, boolean is){
            this.x = x;
            this.h = h;
            this.isStart = is;
        }
    }
    public List<int[]> getSkyline(int[][] buildings) {
        List<int[]> result = new ArrayList<>();
        if(buildings == null || buildings[0] == null || buildings.length == 0)
            return result;
        PriorityQueue<Integer> pq = new PriorityQueue<>(buildings.length, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        List<Edge> edges = new ArrayList<>();
        for(int[] nums : buildings){
            int x1 = nums[0];
            int x2 = nums[1];
            int h = nums[2];
            edges.add(new Edge(x1,h,true));
            edges.add(new Edge(x2,h,false));
        }
        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge l1, Edge l2) {
                if (l1.x != l2.x)
                    return l1.x - l2.x;
                if (l1.isStart && l2.isStart) {
                    return l2.h - l1.h;
                }
                if (!l1.isStart && !l2.isStart) {
                    return l1.h-l2.h;
                }
                return l1.isStart ? -1 : 1;
            }
        });
        for(Edge edge : edges){
            if(edge.isStart){
                if(pq.isEmpty() || edge.h > pq.peek()){
                    int h = edge.h;
                    int x = edge.x;
                    int[] arr = {x,h};
                    result.add(arr);
                }
                pq.add(edge.h);
            }else {
                pq.remove(edge.h);
                int[] arr = new int[2];
                arr[0] = edge.x;
                if(pq.isEmpty() || edge.h > pq.peek()){
                    if(!pq.isEmpty()){
                       arr[1] = pq.peek();
                    }
                }
                result.add(arr);
            }
        }
        return result;
    }
}
