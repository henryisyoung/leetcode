package airbnb;

import java.util.*;

public class GuessNumber {
    private static String target = "3536";

    private static int guessServer(String guess) {
        int res = 0;
        Map<Character, Integer> targetMap = new HashMap<>();

        for (char c : target.toCharArray()) {
            if (targetMap.containsKey(c)) {
                targetMap.put(c, targetMap.get(c) + 1);
            } else {
                targetMap.put(c, 0);
            }
        }
        Map<Character, Integer> guessMap = new HashMap<>();
        for (char c : guess.toCharArray()) {
            if (guessMap.containsKey(c)) {
                guessMap.put(c, guessMap.get(c) + 1);
            } else {
                guessMap.put(c, 0);
            }
        }
        for (char k : guessMap.keySet()) {
            if (targetMap.containsKey(k)) {
                res += Math.min(guessMap.get(k), targetMap.get(k));
            }
        }
        return res;
    }
            
    private static String genNumber(List<Integer> guessed, int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guessed.size(); i++) {
            sb.append(guessed.get(i));
        }
        for (int i = guessed.size(); i < 4; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static String genNumber(List<Integer> guessed) {
        if (guessed == null || guessed.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guessed.size(); i++) {
            sb.append(guessed.get(i));
        }
        return sb.toString();
    }

    public static String guess() {
        List<Integer> res = new ArrayList<>();
        List<Integer> cands = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println("\nstart to guess " + target + " ...");
        System.out.println("res: " + res);
        System.out.println("candList: " + cands);
        int counter = 0;
        Iterator<Integer> iter = cands.iterator();
        while (iter.hasNext() && res.size() < 4) {
            int cand = iter.next();
            counter++;
            int guessedCount = res.size();
            String guessCand = genNumber(res, cand);
            int guessRes = guessServer(guessCand);
            System.out.println("cand: " + cand);
            System.out.println("guessRes: " + guessRes);
            if (guessRes == guessedCount) {
                iter.remove();
            } else if (guessRes > guessedCount) {
                for (int i = guessedCount; i < guessRes; i++) {
                    res.add(cand);
                }
                iter.remove();
            } else {
                // something wrong here
                return genNumber(res);
            }
        }
        //System.out.println(res.size());
        if (res.size() < 4) {
            for (int i = res.size(); i < 4; i++) {
                res.add(6);
            } }
        // System.out.println("guessed " + counter + " times");
        return genNumber(res);
    }

    //
    //Guess Number II
    //
    private static String t = "4361";

    public static void main(String args[]) {
        System.out.println(client());
    }

    public static int check(String guess) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (t.charAt(i) == guess.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    private static String client(){
        char[] result = new char[4];
        Arrays.fill(result, '0');
        String base = "1111";
        System.out.println("Server call: " + base);
        int baseResult = check(base);
        if (baseResult == 4) {
            return base;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 2; j < 6; j++) {
                String newS = replace(base, i, (char) (j + '0'));
                System.out.println("Server call: " + newS);
                int newResult = check(newS);
                if (newResult != baseResult) {
                    result[i] = baseResult > newResult ? '1' : (char) (j + '0');
                    break;
                }
            }
            if (result[i] == '0') {
                result[i] = '6';
            }
        }
        return new String(result);
    }
    private static String replace(String s, int index, char c) {
        char[] arr = s.toCharArray();
        arr[index] = c;
        return new String(arr);
    }
}
