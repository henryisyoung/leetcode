package leetcode.twoPointer;

import java.util.*;

public class ThreeSumII {
    public long threeSum(int[] A, int target) {
        Arrays.sort(A);
        long count = 0;
        int MOD = 1_000_000_007;
        for (int i = 0; i < A.length - 2; i++) {
            int j = i + 1;
            int k = A.length - 1;
            while (j < k) {
                if (A[i] + A[j] + A[k] == target) {

                    if (A[j] == A[k]) {
                        count += (k-j+1) * (k-j) / 2;
                        count %= MOD;
                        break;
                    } else {
                        int left = 1, right = 1;
                        while (j+1 < k && A[j] == A[j+1]) {
                            left++;
                            j++;
                        }
                        while (k-1 > j && A[k] == A[k-1]) {
                            right++;
                            k--;
                        }
                        count += left * right;
                        count %= MOD;
                        j++;
                        k--;
                    }
                } else if (A[i] + A[j] + A[k] < target) {
                    j++;
                }
                else {
                    k--;
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        ThreeSumII sovler = new ThreeSumII();
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        int n = Integer.parseInt(s);
        String s1 = in.nextLine();
        String[] arr = s1.split(" ");
        int[] A = new int[n];
        for (int i = 0; i < n; i++) {
            A[i] = Integer.parseInt(arr[i]);
        }
        int target = in.nextInt();
        long count = sovler.threeSum(A, target);
        System.out.println(count);
    }

}
