package google.vo.mianjing;

public class PairsSongsWithTotalDurationsDivisible {
    public int numPairsDivisibleBy60(int[] time) {
        int[] reminder = new int[60];
        int count = 0;
        for (int num : time) {
            if (num % 60 == 0) {
                count += reminder[0];
            } else {
                count += reminder[60 - num % 60];
            }
            reminder[num % 60]++;
        }
        return count;
    }
}
