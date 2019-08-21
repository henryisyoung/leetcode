package dropbox;

public class DesignHitCounter2 {
    Node[] table;
    /** Initialize your data structure here. */
    public DesignHitCounter2() {
        this.table = new Node[300];
        for (int i = 0; i < 300; i++) table[i] = new Node(0, 0);
    }

    /** Record a hit.
     @param timestamp - The current timestamp (in seconds granularity). */
    public void hit(int timestamp) {
        int index = timestamp % 300;
        if (table[index].timestamp == timestamp) {
            table[index].count++;
        } else {
            table[index].count = 1;
            table[index].timestamp = timestamp;
        }
    }

    /** Return the number of hits in the past 5 minutes.
     @param timestamp - The current timestamp (in seconds granularity). */
    public int getHits(int timestamp) {
        int count = 0;
        for (int i = 0; i < 300; i++) {
            if (table[i].timestamp + 300 > timestamp) {
                count += table[i].count;
            }
        }
        return count;
    }

    class Node{
        int count;
        int timestamp;
        public Node(int count, int timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }
}
