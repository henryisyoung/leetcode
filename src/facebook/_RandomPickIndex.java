package facebook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class _RandomPickIndex {
    //https://leetcode.com/submissions/detail/77060889/
    //https://leetcode.com/problems/shuffle-an-array/
    Map<Integer, List<Integer>> map;
    Random random;
    int[] nums;

    public _RandomPickIndex(int[] nums) {
        this.map = new HashMap<>();
        this.random = new Random();
        for (int i = 0; i < nums.length; i++) {
            map.putIfAbsent(nums[i], new ArrayList<>());
            map.get(nums[i]).add(i);
        }
        this.nums = nums;
    }

    public int pick(int target) {
        int n = map.get(target).size();
        int index = random.nextInt(n);
        return map.get(target).get(index);
    }

    public int pick2(int target) {
        int index = 0;
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                count++;
                if (random.nextInt(count) + 1 == 1) index = i;
            }
        }
        return index;
    }

    public String randomReadFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        int count = 1;
        String result = "";
        Random random = new Random();
        String line;
        while ((line = reader.readLine()) != null) {
            if(random.nextInt(count) == 0) {
                result = line;
            }
            count++;
        }

        return result;
    }

    public static void main(String[] args) {
        Random random = new Random();
        System.out.println(random.nextInt(1));
    }
}