package google.vo;

public class MaximumPointsYouCanObtainCards {
    public static int maxScore(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int[] sumArray = new int[n + 1];
        for (int i = 0; i < n; i++) {
            sumArray[i + 1] = sumArray[i] + cardPoints[i];
        }

        int sum = sumArray[n];
        if (k == n) {
            return sum;
        }
        int min = Integer.MAX_VALUE;
        k = n - k;
        for (int i = k; i <= n; i++) {
            int val = sumArray[i] - sumArray[i - k];
            min = Math.min(min, val);
        }
        return sum - min;
    }

    public static void main(String[] args) {
        int[] cardPoints = {1,2,3,4,5,6,1};
        int k = 3;
        System.out.println(maxScore(cardPoints, k));
    }
}
