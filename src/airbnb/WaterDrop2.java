package airbnb;

//      +
//      ++         +
//      ++   +     +
//      +++ +++   ++
//      ++++++++ +++
public class WaterDrop2 {
    // no infinity wall on each side
    public void pourWater2(int[] heights, int water, int location) {
        if (heights == null || heights.length == 0) {
            return;
        }
        int n = heights.length;
        int[] waters = new int[n];

        while (water > 0) {
            int left = location - 1;
            while (left >= 0) {
                if (heights[left] + waters[left] > heights[left + 1] + waters[left + 1]) {
                    break;
                }
                left--;
            }
            if (left == -1) {
                water--;
                continue;
            }
            if (heights[left + 1] + waters[left + 1] < heights[location] + waters[location]) {
                waters[left + 1]++;
                water--;
                continue;
            }
            int right = location + 1;
            while (right < n) {
                if (heights[right] + waters[right] > heights[right - 1] + waters[right - 1]) {
                    break;
                }
                right++;
            }
            if (right == n) {
                water--;
                continue;
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

    private void print(int[] heights, int[] waters) {
        int n = heights.length;
        int maxHeight = 0;
        for (int i = 0; i < n; i++) {
            maxHeight = Math.max(maxHeight, heights[i] + waters[i]);
        }
        for (int h = maxHeight; h > 0; h--) {
            for (int i = 0; i < n; i++) {
                if (h <= heights[i]) {
                    System.out.print("+");
                } else if (h > heights[i] && h <= heights[i] + waters[i]) {
                    System.out.print("w");
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
        int[] heights2 = {3,1,2,3,2,1};
        int water = 8;
        int location = 5;
        WaterDrop2 wd = new WaterDrop2();
        wd.pourWater2(heights, water, location);
        wd.pourWater2(heights2, water, 2);
    }
}