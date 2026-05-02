package snowflake.mianjing.selfreview;
/*
Finding the Closest Cake and Optimal Matching
Introduction to the Challenge
You are given a list (array) representing a line.

In Task 1:

0 represents an empty spot.
1 represents a cake.
In Task 2:

0 represents an empty spot.
1 represents a person.
2 represents a cake.
There are two parts to this interview:

Find the distance to the nearest cake for a specific person.
Pair every person with a unique cake so the total distance for everyone is as small as possible.

Task 1: Nearest Cake Distance
Requirement
You are given:

A: A binary array (1 is a cake, 0 is not).
start: The index where a person is standing.
You must return the shortest distance from the start position to any cake. If there are no cakes in the entire array, return -1.

Sample Input and Output
A = [0, 0, 1, 0, 0, 1, 0]
start = 0
Output:

2
Explanation: The closest cake is at index 2. The distance is 2 - 0 = 2.

Task 2: Global Matching
Requirement
Now the input array contains both persons and cakes:

1 = person
2 = cake
0 = empty
You must pair each person with exactly one unique cake. The goal is to make the sum of distances for all pairs as small as possible.

Rules:

Minimize the total distance sum.
If there are more persons than cakes, return an error (impossible).
If asked about a specific person, return the index of the cake assigned to them in this best-case scenario.
Note: You cannot simply pick the nearest cake for each person individually. One person's choice might force someone else to walk much further, increasing the total cost. You must look at the global picture.

Sample Scenario
line = [1, 2, 0, 1, 0, 0, 2]
# Persons are at indices [0, 3]
# Cakes are at indices [1, 6]
Individual Nearest Choices:

Person 0 wants Cake 1 (Distance 1).
Person 3 also wants Cake 1 (Distance 2).
They cannot both have Cake 1.

Global Optimal Assignment:

Person 0 takes Cake 1 (Distance 1).
Person 3 takes Cake 6 (Distance 3).
Total Distance: 1 + 3 = 4.

If you query for Person 3, the correct answer is Cake 6, even though Cake 1 is closer.


follow up：记不太清了，大概是每个人去吃离他最近的蛋糕，给定一个人的index，问这个人能吃到哪个蛋糕。同时给了限定条件：1.不会有两个人到同一个蛋糕的位置相等
2.不会有两个蛋糕到同一个人的位置相等。也就是说人吃到的蛋糕是固定的，不会出现多种选择。我是把array扫了两遍，把每个人到每个蛋糕的距离存到priority queue里面去，
同时维护两个set记录被吃掉的蛋糕和已吃蛋糕的人。不知道有没有更好的解法了
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClosestCakeAndGlobalAssignment2 {

    /* ---------------------------------------------------------------------
     * Part 1: Nearest Cake Distance
     *
     * Two-pointer / radial scan from `start`. Step d outward and check both
     * sides; the first cake hit is the closest. O(n) time, O(1) space.
     * ------------------------------------------------------------------- */
    public int nearestCake(int[] A, int start) {
        int n = A.length;
        for (int d = 0; d < n; d++) {
            int l = start - d, r = start + d;
            if (l >= 0 && A[l] == 1) return d;
            if (r < n && A[r] == 1) return d;
        }
        return -1;
    }

    /* ---------------------------------------------------------------------
     * Part 2: Global Optimal Assignment
     *
     * Input array contains 0 (empty), 1 (person), 2 (cake) on a line.
     * Pair every person with exactly one unique cake to minimise the total
     * sum of |personIdx - cakeIdx|.
     *
     * Key observation
     * ---------------
     * On a 1-D line, the optimal pairing never "crosses". Sort persons P
     * (length n) and cakes C (length m, m >= n). In the optimum, if we pick
     * any subset of n cakes, the i-th person (in sorted order) is matched to
     * the i-th picked cake (in sorted order). So we only need to choose
     * which n of the m cakes to use.
     *
     * DP
     * --
     * dp[i][j] = minimum total distance using the first i persons and only
     *            the first j cakes (some of those cakes may go unused).
     *
     * Transition:
     *   dp[i][j] = min(
     *     dp[i][j-1],                                  // leave cake j unused
     *     dp[i-1][j-1] + |P[i-1] - C[j-1]|             // assign cake j to person i
     *   )
     *
     * Base:
     *   dp[0][j] = 0   (no persons -> nothing to pay)
     *   dp[i][j] = +INF for i > j (not enough cakes)
     *
     * Answer: dp[n][m]. Time O(n*m), space O(n*m) (easy to roll to O(m)
     * if we only need the total). We keep the full table to reconstruct
     * which cake each person got, so we can answer "which cake was
     * assigned to person X?" in O(n+m).
     * ------------------------------------------------------------------- */
    public int GlobalAssignment(int[] line) throws Exception {
        if (line == null) {
            return 0;
        }
        List<Integer> people = new ArrayList<>();
        List<Integer> cakes = new ArrayList<>();
        for (int i = 0 ; i < line.length; i++) {
            if (line[i] == 1) {
                people.add(i);
            }
            if (line[i] == 2) {
                cakes.add(i);
            }
        }
        if (people.size() > cakes.size()) {
            throw new Exception("Not enough cakes");
        }
        int size = line.length;
        int m = people.size(), n = cakes.size();
        int[][] dp = new int[m + 1][n + 1];
        for (int[] row : dp) Arrays.fill(row, size * size);

        for (int i = 0; i <= n; i++) {
            dp[0][i] = 0;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int select = dp[i - 1][j - 1] + Math.abs(people.get(i - 1) - cakes.get(j - 1));
                int skip = dp[i][j - 1];
                dp[i][j] = Math.min(select, skip);
            }
        }

        return dp[m][n];
    }

    public int[] GlobalAssignmentWithDetails(int[] line) throws Exception {
        if (line == null) {
            return new int[0];
        }
        List<Integer> people = new ArrayList<>();
        List<Integer> cakes = new ArrayList<>();
        for (int i = 0 ; i < line.length; i++) {
            if (line[i] == 1) {
                people.add(i);
            }
            if (line[i] == 2) {
                cakes.add(i);
            }
        }
        if (people.size() > cakes.size()) {
            throw new Exception("Not enough cakes");
        }
        int size = line.length;
        int m = people.size(), n = cakes.size();
        int[][] dp = new int[m + 1][n + 1];
        for (int[] row : dp) Arrays.fill(row, size * size);

        for (int i = 0; i <= n; i++) {
            dp[0][i] = 0;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int select = dp[i - 1][j - 1] + Math.abs(people.get(i - 1) - cakes.get(j - 1));
                int skip = dp[i][j - 1];
                dp[i][j] = Math.min(select, skip);
            }
        }

        int[] assignment = new int[m];
        int i = m, j = n;
        while (i > 0) {
            if (dp[i][j] == dp[i][j - 1]) {
                j--;
            } else if (dp[i][j] == dp[i - 1][j - 1] + Math.abs(people.get(i - 1) - cakes.get(j - 1))) {
                assignment[i - 1] = cakes.get(j - 1);
                i--;
                j--;
            }
        }
        return assignment;
    }

}
