package apple;

import java.util.Arrays;

public class VideoStitching {
    public int videoStitching(int[][] clips, int time) {
        int count = 0;
        if (clips == null || clips.length == 0 || clips[0] == null || clips[0].length == 0) {
            return count;
        }
        Arrays.sort(clips, (a, b) -> (a[0] - b[0]));
        int max = 0, i = 0, n = clips.length;
        while (max < time) {
            int local = 0;
            while (i < n && clips[i][0] <= max) {
                local = Math.max(local, clips[i++][1]);
            }
            if (local <= max) return -1;
            max = local;
            count++;
        }

        return count;
    }
}
