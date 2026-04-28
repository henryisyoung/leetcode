package snowflake;

import java.util.*;

/**
 * LeetCode 1494 Parallel Courses II.
 * Prerequisites stored as lists; DP state is an int id in [0, 2^n) (same states as a
 * subset of courses, but no prerequisite bitmasks). Subsets of a semester are built
 * by recursion over a list of currently available courses.
 */
public class ParallelCourses2 {
    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        @SuppressWarnings("unchecked")
        List<Integer>[] prereqs = new List[n];
        for (int i = 0; i < n; i++) {
            prereqs[i] = new ArrayList<>();
        }
        for (int[] rel : relations) {
            prereqs[rel[1] - 1].add(rel[0] - 1);
        }

        int fullMask = (1 << n) - 1;
        int[] dp = new int[1 << n];
        Arrays.fill(dp, n + 1);
        dp[0] = 0;

        List<Integer> ready = new ArrayList<>(n);
        for (int mask = 0; mask <= fullMask; mask++) {
            if (dp[mask] > n) {
                continue;
            }
            ready.clear();
            for (int course = 0; course < n; course++) {
                if (isCompleted(mask, course)) {
                    continue;
                }
                if (allPrereqsSatisfied(mask, prereqs[course])) {
                    ready.add(course);
                }
            }
            relaxSemesterChoices(mask, ready, 0, 0, 0, k, dp);
        }
        return dp[fullMask];
    }

    /** Completed courses are encoded in {@code mask}: bit {@code course} is 1 iff done. */
    private static boolean isCompleted(int mask, int course) {
        return (mask & (1 << course)) != 0;
    }

    private static boolean allPrereqsSatisfied(int mask, List<Integer> prereqCourses) {
        for (int p : prereqCourses) {
            if (!isCompleted(mask, p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Enumerate every non-empty subset of {@code ready} with size ≤ {@code k}; each
     * choice advances the DP by one semester.
     */
    private static void relaxSemesterChoices(
            int mask,
            List<Integer> ready,
            int index,
            int picked,
            int nextMask,
            int k,
            int[] dp) {
        if (index == ready.size()) {
            if (picked >= 1 && picked <= k) {
                int combined = mask | nextMask;
                dp[combined] = Math.min(dp[combined], dp[mask] + 1);
            }
            return;
        }
        // Skip this course this semester.
        relaxSemesterChoices(mask, ready, index + 1, picked, nextMask, k, dp);
        // Take this course this semester.
        int course = ready.get(index);
        relaxSemesterChoices(mask, ready, index + 1, picked + 1, nextMask | (1 << course), k, dp);
    }
}
