package datastructure.Heap;

import java.util.Arrays;
import java.util.PriorityQueue;

public class CourseScheduleIII {
    public int scheduleCourse(int[][] courses) {
        Arrays.sort(courses, (a, b) -> (a[1] - b[1]));
        PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> (b -a));
        int cur = 0;
        for (int[] course : courses) {
            cur += course[0];
            pq.add(course[0]);
            if (cur > course[1]) {
                if (!pq.isEmpty()) {
                    cur -= pq.poll();
                }
            }
        }
        return pq.size();
    }
}
