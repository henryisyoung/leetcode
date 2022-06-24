package apple;

public class HitCounter {
    int[] table, counter;
    public HitCounter() {
        this.counter = new int[300];
        this.table = new int[300];
    }

    public void hit(int timestamp) {
        if(table[timestamp % 300] == 0) {
            table[timestamp % 300] = timestamp;
            counter[timestamp % 300]++;
        } else {
            if (table[timestamp % 300] == timestamp) {
                counter[timestamp % 300]++;
            } else {
                table[timestamp % 300] = timestamp;
                counter[timestamp % 300] = 1;
            }
        }
    }

    public int getHits(int timestamp) {
        int count = 0;
        for (int i = 0; i < 300; i++) {
            if (table[i] + 300 > timestamp) {
                count += counter[i];
            }
        }
        return count;
    }
}
