package leetcode.graphAndSearch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class TheMazeIII {
    class Point implements Comparable<Point> {
        int x;
        int y;
        int dis;  // distance from ball
        String path;  // directions from ball
        Point(int x, int y, int dis, String path) {
            this.x = x;
            this.y = y;
            this.dis = dis;
            this.path = path;
        }
        // if both ways have shortest distance, they should be ordered lexicographically
        public int compareTo(Point point) {
            return this.dis == point.dis ? this.path.compareTo(point.path) : this.dis - point.dis;
        }
    }

    public String findShortestWay(int[][] maze, int[] ball, int[] hole) {
        return "impossible";
    }
}
