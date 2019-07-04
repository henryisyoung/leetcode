package leetcode.greedy;

import java.util.Collections;
import java.util.PriorityQueue;

public class AssignCookies {
    public static int findContentChildren(int[] g, int[] s) {
        if(g == null || s == null || s.length * g.length == 0) {
            return 0;
        }
        PriorityQueue<Integer> children = new PriorityQueue<>(g.length, Collections.reverseOrder());
        PriorityQueue<Integer> cookies = new PriorityQueue<>(s.length, Collections.reverseOrder());
        for (int i : g) {
            children.add(i);
        }
        for (int i : s) {
            cookies.add(i);
        }
        int count = 0;
        while (!children.isEmpty() && !cookies.isEmpty()) {
            int child = children.poll();
//            System.out.println("child " + child + " cookies " + cookies.peek());
            if (cookies.peek() >= child) {
                count++;
                cookies.poll();
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] g = {1,2,3}, s = {1,1};
        System.out.println(findContentChildren(g,s));
    }
}
