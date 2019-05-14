package Twitter;

import java.util.*;

public class MyCalendarTwo {
    List<Node> nodes;
    public MyCalendarTwo() {
        this.nodes = new ArrayList<>();
    }

    public boolean book(int start, int end) {
//        System.out.println("start " + start + " end " + end);
        int count = 0;
        List<Node> copy = new ArrayList<>(nodes);
        copy.add(new Node(start, true));
        copy.add(new Node(end, false));
        Collections.sort(copy, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.pos == o2.pos) {
                    if (!o1.isStart) {
                        return -1;
                    }
                    return 1;
                } else {
                    return o1.pos - o2.pos;
                }
            }
        });
//        System.out.println(copy.toString());
        for (Node n : copy) {
//            System.out.println(n.toString());
            if (n.isStart) {
                count++;
//                System.out.println(count);
                if (count >= 3){
                    return false;
                }
            } else {
                count--;
            }
        }
        nodes = new ArrayList<>(copy);
        return true;
    }

    class Node {
        int pos;
        boolean isStart;
        public Node(int pos, boolean isStart) {
            this.isStart = isStart;
            this.pos = pos;
        }

        @Override
        public String toString() {
            return "pos " + pos + " isstart " + isStart;
        }
    }

    public static void main(String[] args) {
        int[][] nums = {{10,20},{50,60},{10,40},{5,15},{5,10},{25,55}};
        MyCalendarTwo solver = new MyCalendarTwo();
        for (int[] num : nums) {
            System.out.println(solver.book(num[0], num[1]));
        }

        //[true,true,true,false,true,true]
    }
}
