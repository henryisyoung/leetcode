package snap;

public class HitCounter {
    int[] counter, timeStamps;
    /** Initialize your data structure here. */
    public HitCounter() {
        this.counter = new int[300];
        this.timeStamps = new int[300];
    }

    /** Record a hit.
     @param timestamp - The current timestamp (in seconds granularity). */
    public void hit(int timestamp) {
        int index = timestamp % 300;
        if (timeStamps[index] == timestamp) {
            counter[index]++;
        } else {
            counter[index] = 1;
            timeStamps[index] = timestamp;
        }
    }

    /** Return the number of hits in the past 5 minutes.
     @param timestamp - The current timestamp (in seconds granularity). */
    public int getHits(int timestamp) {
        int count = 0;
        for (int i = 0; i < 300; i++) {
            if (timeStamps[i] + 300 > timestamp) {
                count += counter[i];
            }
        }
        return count;
    }
}
