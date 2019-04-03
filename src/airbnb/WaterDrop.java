package airbnb;

//      +
//      ++         +
//      ++   +     +
//      +++ +++   ++
//      ++++++++ +++
public class WaterDrop {
    public void pourWater2(int[] heights, int water, int location) {
        if (heights == null || heights.length <= location) {
            return;
        }
        int n = heights.length;
        int[] waters = new int[n];
        while (water > 0) {
            int left = location - 1;
            while (left >= 0 ) {
                if (heights[left] + waters[left] > heights[left + 1] + waters[left + 1]) {
                    break;
                }
                left--;
            }
            if (heights[left + 1] + waters[left + 1] < heights[location] + waters[location]) {
                waters[left + 1]++;
                water--;
                continue;
            }

            int right = location + 1;
            while (right < n) {
                if (heights[right - 1] + waters[right - 1] < heights[right] + waters[right]) {
                    break;
                }
                right++;
            }
            if (heights[right - 1] + waters[right - 1] < heights[location] + waters[location]) {
                waters[right - 1]++;
                water--;
                continue;
            }
            waters[location]++;
            water--;
        }
        print(heights, waters);
    }

    public void pourWater(int[] heights, int water, int location) {
        int[] waters = new int[heights.length];
        int pourLocation;
        while (water > 0) {
            int left = location - 1;
            while (left >= 0) {
                if (heights[left] + waters[left] > heights[left + 1] + waters[left+ 1]) {
                    break;
                }
                left--;
            }

            if (heights[left + 1] + waters[left + 1] < heights[location] + waters[location]) {
                pourLocation = left + 1;
                waters[pourLocation]++;
                water--;
                continue;
            }
            int right = location + 1;
            while (right < heights.length) {
                if (heights[right] + waters[right] > heights[right - 1] + waters[right - 1]) {
                    break;
                }
                right++;
            }
            if (heights[right - 1] + waters[right - 1] < heights[location] + waters[location]) {
                pourLocation = right - 1;
                waters[pourLocation]++;
                water--;
                continue;
            }
            pourLocation = location;
            waters[pourLocation]++;
            water--;
        }
        print(heights, waters);
    }

    private void print(int[] heights, int[] waters) {
        int n = heights.length;
        int maxHeight = 0;
        for (int i = 0; i < n; i++) {
            maxHeight = Math.max(maxHeight, heights[i] + waters[i]);
        }
        for (int height = maxHeight; height > 0; height--) {
            for (int i = 0; i < n; i++) {
                if (height <= heights[i]) {
                    System.out.print("+");
                } else if (height > heights[i] && height <= heights[i] + waters[i]) {
                    System.out.print("W");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] heights = {5,4,2,1,2,3,2,1,0,1,2,4};
        int water = 9;
        int location = 5;
        WaterDrop wd = new WaterDrop();
        wd.pourWater2(heights, water, location);
    }
}