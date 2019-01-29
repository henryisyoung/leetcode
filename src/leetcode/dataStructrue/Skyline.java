package leetcode.dataStructrue;

import java.util.*;

public class Skyline {
    private class Edge{
        int height, pos;
        boolean isStart;

        public Edge(int pos, int height, boolean isStart){
            this.pos = pos;
            this.height = height;
            this.isStart = isStart;
        }
    }

    private class EdgeComparator implements Comparator<Edge> {
        @Override
        public int compare(Edge arg1, Edge arg2) {
            Edge l1 = (Edge) arg1;
            Edge l2 = (Edge) arg2;
            if (l1.pos != l2.pos)
                return compareInteger(l1.pos, l2.pos);
            if (l1.isStart && l2.isStart) {
                return compareInteger(l2.height, l1.height);
            }
            if (!l1.isStart && !l2.isStart) {
                return compareInteger(l1.height, l2.height);
            }
            return l1.isStart ? -1 : 1;
        }

        int compareInteger(int a, int b) {
            return a <= b ? -1 : 1;
        }
    }



    public List<int[]> getSkyline(int[][] buildings) {
        List<int[]> rlt = new ArrayList<int[]>();

        if(buildings == null || buildings.length == 0 || buildings[0].length == 0){
            return rlt;
        }
        ArrayList<Edge> edgeList = new ArrayList<Edge>();
        for(int[] edge : buildings){
            Edge e1 = new Edge(edge[0], edge[2], true);
            Edge e2 = new Edge(edge[1], edge[2], false);
            edgeList.add(e1);
            edgeList.add(e2);
        }
        Collections.sort(edgeList, new EdgeComparator());

        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(buildings.length, new Comparator<Integer>(){
            public int compare(Integer a, Integer b){
                return b - a;
            }
        });


        for(Edge edge : edgeList){
            if(edge.isStart){
                if(pq.isEmpty() || edge.height > pq.peek()){
                    int[] cur = new int[2];
                    cur[0] = edge.pos;
                    cur[1] = edge.height;
                    rlt.add(cur);
                }
                pq.offer(edge.height);
            }else{
                pq.remove(edge.height);
                if(pq.isEmpty() || pq.peek() < edge.height){
                    int[] cur = new int[2];
                    if(pq.isEmpty()){
                        cur[0] = edge.pos;
                        cur[1] = 0;
                        rlt.add(cur);
                    }else{
                        cur[0] = edge.pos;
                        cur[1] = pq.peek();
                        rlt.add(cur);
                    }
                }
            }
        }
        return rlt;
    }
}
