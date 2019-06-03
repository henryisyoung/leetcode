package leetcode.dataStructrue;

import java.util.*;

public class BullsAndCows {
    public String getHint(String secret, String guess) {
        if (secret == null || guess == null || secret.length() != guess.length()) {
            return "";
        }
        int A = 0;
        int[] tableA = new int[10], tableB = new int[10];
        for (int i = 0; i < secret.length(); i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                A++;
            }
            int a = secret.charAt(i) - '0', b = guess.charAt(i) - '0';
            tableA[a]++;
            tableB[b]++;
        }
        int B = 0;
        for (int i = 0; i < 10; i++) {
            B += Math.min(tableA[i], tableB[i]);
        }
        return A + "A" + (B - A) + "B";
    }

    public static void main(String[] args) {
        String secret = "1807", guess = "7810";
        BullsAndCows solver = new BullsAndCows();
        System.out.println(solver.getHint(secret, guess));
    }
}
