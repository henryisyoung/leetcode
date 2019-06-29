package leetcode.twoPointer;

import java.util.Arrays;

public class BoatsToSavePeople {
    public int numRescueBoats(int[] people, int limit) {
        if (people == null || people.length == 0) {
            return 0;
        }
        Arrays.sort(people);
        int count = 0;
        int i = 0, j = people.length - 1;
        while (i <= j) {
            count++;
            if (people[i] + people[j] <= limit) {
                i++;
            }
            j--;
        }
        return count;
    }
}
