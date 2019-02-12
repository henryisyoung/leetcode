package leetcode.twoPointer;

public class TwoSumII {
    public int[] twoSum(int[] numbers, int target) {
        if(numbers == null || numbers.length < 2){
            return null;
        }
        int[] result = new int[2];

        int start = 0, end = numbers.length - 1;
        while (start + 1 < end){
            int val = numbers[start] + numbers[end];
            if(val == target){
                result[0] = start + 1;
                result[1] = end + 1;
                return result;
            } else if(val < target){
                start++;
                while (numbers[start] == numbers[start-1]){
                    start++;
                }
            }else {
                end--;
                while (numbers[end] == numbers[end+1]){
                    end--;
                }
            }
        }
        result[0] = start + 1;
        result[1] = end + 1;
        return result;
    }
    public int[] twoSum2(int[] numbers, int target) {
        if (numbers == null || numbers.length == 0) {
            return null;
        }
        int[] result = new int[2];
        int start = 0, end = numbers.length - 1;
        while (start + 1 < end) {
            int sum = numbers[start] + numbers[end];
            if (sum == target) {
                result[0] = start + 1;
                result[1] = end + 1;
                return result;
            } else if (sum > target) {
                end--;
                while (start + 1 < end && numbers[end] == numbers[end + 1]) {
                    end--;
                }
            } else {
                start++;
                while (start + 1 < end && numbers[start] == numbers[start - 1]) {
                    start--;
                }
            }
        }
        result[0] = start + 1;
        result[1] = end + 1;
        return result;
    }
}
