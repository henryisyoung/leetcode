package airbnb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuessNumberII {
    private static String t = "4361";

    public static void main(String args[]) {
        System.out.println(client());
    }

    private static String client(){
        List<String> candidates = Arrays.asList("1111", "2222", "3333", "4444" ,"5555");
        StringBuilder sb = new StringBuilder();
        int sum = 4;

        for (int i = 0; i < candidates.size(); i++) {
            int count = checkDigits(candidates.get(i));
            System.out.println("count " + count);
            if (count == 4) {
                sb.append(candidates.get(i));
                return sb.toString();
            }
            sum -= count;
            for (int j = 0; j < count; j++) {
                sb.append(i + 1);
            }
        }
        for (int j = 0; j < sum; j++) {
            sb.append('6');
        }
        return sb.toString();
    }

    private static int checkDigits(String s) {
        int count  = 0;
        Map<Character, Integer> sourceMap = new HashMap<>(), targetMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            if (!sourceMap.containsKey(c)) {
                sourceMap.put(c, 1);
            } else {
                sourceMap.put(c, 1 + sourceMap.get(c));
            }
        }

        for (char c : t.toCharArray()) {
            if (!targetMap.containsKey(c)) {
                targetMap.put(c, 1);
            } else {
                targetMap.put(c, 1 + targetMap.get(c));
            }
        }

        for (char c : sourceMap.keySet()) {
            if (targetMap.containsKey(c)) {
                count += Math.min(targetMap.get(c), sourceMap.get(c));
            }
        }
        return count;
    }
}
