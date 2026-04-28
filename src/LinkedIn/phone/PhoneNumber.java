package LinkedIn.phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Problem You are given: A list of lowercase English words, knownWords. A digit string, phoneNumber,
containing characters '0'–'9'. On a traditional mobile keypad: 2 → "abc", 3 → "def", …, 9 → "wxyz". 0 → space. 1 → no letters.
A word matches if every digit in phoneNumber maps to a letter such that the sequence of mapped letters forms the word exactly
(with no extra or missing characters).
Return all matching words from knownWords in any order. Example Input: knownWords = ["aa", "ab", "ba", "qq", "hello", "b"] phoneNumber = "1221" Output: ["aa", "ab", "ba"] Explanation: The number 1221 reduces to 22 after removing both '1's. Digit 2 maps to a, b, or c. The only two-letter words composed of those letters are "aa", "ab", and "ba".
 */
public class PhoneNumber {
    public static List<String> matchingWords(List<String> knownWords, String phoneNumber) {
        String[] map = new String[10];
        map[0] = " ";
        map[1] = "";
        map[2] = "abc";
        map[3] = "def";
        map[4] = "ghi";
        map[5] = "jkl";
        map[6] = "mno";
        map[7] = "pqrs";
        map[8] = "tuv";
        map[9] = "wxyz";

        StringBuilder filtered = new StringBuilder();
        for (char c : phoneNumber.toCharArray()) {
            if (c != '1') filtered.append(c);
        }
        List<String> result = new ArrayList<>();
        String digits = filtered.toString();
        if (digits.contains(" ")) return result;

        outer:
        for (String word : knownWords) {
            if (word.length() != digits.length()) continue;

            for (int i = 0 ; i < word.length(); i++) {
                char curChar = word.charAt(i);
                int digit = digits.charAt(i) - '0';
                if (map[digit].indexOf(curChar) == -1) {
                    continue outer;
                }
            }
            result.add(word);

        }

        return result;
    }

    public static void main(String[] args) {
        List<String> knownWords =
                new ArrayList<>(Arrays.asList("aa", "ab", "ba", "qq", "hello", "b"));
        String phoneNumber = "1221";
        System.out.println(matchingWords(knownWords, phoneNumber));
    }

}
