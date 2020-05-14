package lyft;

import java.util.Arrays;
import java.util.Stack;

public class AsteroidCollision {
    public static int[] asteroidCollision(int[] asteroids) {
        if (asteroids == null || asteroids.length == 0) return asteroids;
        Stack<Integer> stack = new Stack<>();
        int index = 0, n = asteroids.length;
        while (index < n) {
            int cur = asteroids[index++];
            if (cur < 0) {
                while (!stack.isEmpty() && cur + stack.peek() < 0 && stack.peek() > 0){
                    stack.pop();
                }
                if (stack.isEmpty() || stack.peek() < 0) {
                    stack.push(cur);
                } else if (cur + stack.peek() == 0) {
                    stack.pop();
                }
            } else {
                stack.push(cur);
            }
        }
        int size = stack.size();
        int[] result = new int[size];
        while (!stack.isEmpty()) {
            result[--size] = stack.pop();
        }
        return result;
    }

    public static int[] asteroidCollision2(int[] asteroids) {
        if (asteroids == null || asteroids.length == 0) return asteroids;
        int top = -1, n = asteroids.length;
        int[] result = new int[n];
        for (int cur : asteroids) {
            if (cur > 0) {
                result[++top] = cur;
            } else {
                while (top != -1 && cur + result[top] < 0 && result[top] > 0) {
                    top--;
                }
                if (top == -1 || result[top] < 0) result[++top] = cur;
                else if (result[top] + cur == 0) top--;
            }
        }
        return Arrays.copyOf(result, top + 1);
    }

    public static void main(String[] args) {
        int[] result = asteroidCollision(new int[]{5, 10, -5});
        int[] result2 = asteroidCollision(new int[]{5, -5});
        int[] result3 = asteroidCollision(new int[]{10, 2, -5});
        int[] result4 = asteroidCollision(new int[]{-2, -1, 1, 2});
        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(result2));
        System.out.println(Arrays.toString(result3));
        System.out.println(Arrays.toString(result4));
    }
}
