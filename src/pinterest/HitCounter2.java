package pinterest;

public class HitCounter2 {
    int[] timestamps, hits;

    /** Initialize your data structure here. */
    public HitCounter2() {
        this.hits = new int[300];
        this.timestamps = new int[300];
    }

    /** Record a hit.
     @param timestamp - The current timestamp (in seconds granularity). */
    public void hit(int timestamp) {
        int index = timestamp % 300;
        if (timestamps[index] != timestamp) {
            timestamps[index] = timestamp;
            hits[index] = 1;
        } else {
            hits[index]++;
        }
    }

    /** Return the number of hits in the past 5 minutes.
     @param timestamp - The current timestamp (in seconds granularity). */
    public int getHits(int timestamp) {
        int count = 0;
        for (int i = 0; i < 300; i++) {
            if(timestamp - timestamps[i] < 300) {
                count += hits[i];
            }
        }
        return count;
    }
}
