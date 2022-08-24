package airbnb.Phone;

public class PourWater {
    public int[] pourWater(int[] heights, int V, int K) {
        if(heights == null || heights.length == 0 || V == 0) {
            return heights;
        }

        while (V > 0) {
            int pos = findLeftPos(heights, K);
            if (pos == K) {
                pos = findRightPos(heights, K);
            }
            heights[pos]++;
            V--;
        }
        return heights;
    }

    private int findRightPos(int[] heights, int k) {
        int pos = k;
        int index = k, lowBar = heights[k], len = heights.length;
        while (pos < len) {
            int localHeight = heights[pos];
            if (localHeight < lowBar) {
                lowBar = localHeight;
                index = pos;
            }
            if (pos < len && pos != k && heights[pos - 1] < heights[pos]) {
                break;
            }
            pos++;
        }
        return index;
    }

    private int findLeftPos(int[] heights, int k) {
        int pos = k;
        int index = k, lowBar = heights[k];

        while (pos >= 0) {
            int localHeight = heights[pos];
            if (localHeight < lowBar) {
                lowBar = localHeight;
                index = pos;
            }

            if (pos >= 0 && pos != k && heights[pos] > heights[pos + 1]) {
                break;
            }
            pos--;
        }
        return index;
    }
}
