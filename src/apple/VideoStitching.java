package apple;

import java.util.Arrays;

public class VideoStitching {
    public int videoStitching(int[][] clips, int time) {
        Arrays.sort(clips, (a, b) -> a[0] - b[0]);

        //initially maxReachable clip time is zero
        //and count of clips is zero
        int maxReachable = 0;
        int count = 0;
        //i is index for traversing over clips
        int i = 0;

        //loop until condition met
        while(maxReachable < time) {
            //locally we don't know max where we will reach
            //so mark -1
            int localMaxReachable = -1;
            //while i is in range
            //go through clips with starting point less than maxReachable time
            //because these clips are reachable
            while(i<clips.length && clips[i][0] <= maxReachable) {
                //find the max reachable point from all these clips
                localMaxReachable = Math.max(localMaxReachable, clips[i][1]);
                i++;
            }
            //if after all this we still cannot reach further than current reachable
            //then we are stuck and therefore return -1
            if(localMaxReachable <= maxReachable)   return -1;

            //otherwise, new maxReachable should be changed
            maxReachable = localMaxReachable;
            //and we will increase count of clips needed
            count++;
        }

        return count;
    }
}
