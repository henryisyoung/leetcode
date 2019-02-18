package airbnb;

/**
 * learn
 * 1. 不能用data structure 用最大最小值
 * 2. 注意overstack 所以用long 不是用int
 * 3. binary search 注意corner case
 */
public class FindMedianInLargeFileOfIntegers {
    public double findMedian(int[] nums) {
        int len = 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int num : nums) {
            len++;
            max = Math.max(max, num);
            min = Math.min(min, num);
        }
        if (len % 2 == 1) {
            return (double) search(nums,len / 2 + 1,  min, max);
        } else{
            return 0.5 * search( nums,len / 2 + 1,  min, max) +
                    0.5 * search( nums,len / 2 ,  min, max);
        }
    }

    private long search(int[] nums, int k, long min, long max) {
        if (min >= max) {
            return min;
        }
        long guess = min + (max - min) / 2;
        long result = min, count = 0;
        for (int i : nums) {
            if (i <= guess) {
                count++;
                result = Math.max(result, i);
            }
        }
        if (count == k) {
            return result;
        } else if (count < k) {
            return search(nums, k, Math.max(guess, result + 1), max); // corner case
        } else {
            return search(nums, k, min, result);
        }
    }

    public static void main(String[] args) {
        FindMedianInLargeFileOfIntegers finder = new FindMedianInLargeFileOfIntegers();
        int[] arr = {Integer.MIN_VALUE, 1,2,3,4,5, 6,Integer.MAX_VALUE};
        double result = finder.findMedian(arr);
        System.out.println(result);
    }
}
