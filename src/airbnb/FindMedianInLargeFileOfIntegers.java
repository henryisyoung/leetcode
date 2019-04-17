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
        long max = Integer.MAX_VALUE, min = Integer.MIN_VALUE;
        for (int num : nums) {
            len++;
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
        System.out.println("guess " + guess);
        long result = min, count = 0;
        for (int i : nums) {
            if (i <= guess) {
                count++;
                result = Math.max(result, i);
            }
        }
        System.out.println("k " + k + " count " + count + " result " +result);
        if (count == k) {
            return result;
        } else if (count < k) {
            return search(nums, k, guess + 1, max); // corner case max = 1 min = 0 infinity loop
        } else {
            return search(nums, k, min, result);
        }
    }

    public static void main(String[] args) {
        FindMedianInLargeFileOfIntegers finder = new FindMedianInLargeFileOfIntegers();
        int[] arr = {Integer.MIN_VALUE, 11,221,22,3,34,5, 8, 16,Integer.MAX_VALUE}; //,Integer.MAX_VALUE
        int[] arr2 = {10, 16, 10,10,10,10,10,10,12};
//        double result = finder.findMedian(arr);
        double result2 = finder.findMedian(arr2);
//        System.out.println(result);
        System.out.println(result2);
//        long max = Integer.MAX_VALUE, min = Integer.MIN_VALUE;
//        long guess = min + (max - min) / 2;
//        System.out.println(max);
    }
}
