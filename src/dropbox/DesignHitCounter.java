package dropbox;

public class DesignHitCounter {
    int timeSpan;
    int[] counters, timeStamps;
    public DesignHitCounter(int timeSpan) {
        this.timeSpan = timeSpan;
        this.counters = new int[timeSpan];
        this.timeStamps = new int[timeSpan];
    }

    public void udpate(int timeStamp) {
        if (timeStamps[timeStamp % timeSpan] != timeStamp) {
            counters[timeStamp % timeSpan] = 1;
            timeStamps[timeStamp % timeSpan] = timeStamp;
        } else {
            counters[timeStamp % timeSpan]++;
        }
    }

    public int getCounts(int timeStamp) {
        int count = 0;
        for (int i = 0; i < timeSpan; i++) {
            if (timeStamps[i] > timeStamp - timeSpan) {
                count += counters[i];
            }
        }
        return count;
    }
}
