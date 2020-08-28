package apple;

public class HitCounter {
    int[] counter, table;
    /** Initialize your data structure here. */
    public HitCounter() {
        this.counter = new int[300];
        this.table = new int[300];
    }

    /** Record a hit.
     @param timestamp - The current timestamp (in seconds granularity). */
    public void hit(int timestamp) {
        if (counter[timestamp % 300] == 0) {
            counter[timestamp % 300]++;
        } else {
            if(table[timestamp % 300] == timestamp){
                counter[timestamp % 300]++;
            } else {
                counter[timestamp % 300] = 1;
            }
        }
        table[timestamp % 300] = timestamp;
    }

    /** Return the number of hits in the past 5 minutes.
     @param timestamp - The current timestamp (in seconds granularity). */
    public int getHits(int timestamp) {
        int count = 0;
        for (int i = 0; i < 300;i++) {
            if (table[i] + 300 > timestamp) count += counter[i];
        }
        return count;
    }
}
