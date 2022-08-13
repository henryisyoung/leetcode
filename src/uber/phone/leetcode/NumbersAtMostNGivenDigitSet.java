package uber.phone.leetcode;

public class NumbersAtMostNGivenDigitSet {
    public int atMostNGivenDigitSet(String[] digits, int n) {
        int result = 0;
        int len = Integer.toString(n).length();
        int d = digits.length;
        for (int i = 1; i < len; i++) {
            result += Math.pow(d, i);
        }

        for (int i = 0; i < len; i++) {
            boolean hasSame = false;
            int cur = Integer.toString(n).charAt(i) - '0';
            for (String digit : digits) {
                int num = Integer.parseInt(digit);
                if (cur > num) {
                    result += Math.pow(d, len - 1 - i);
                } else if (cur == num) {
                    hasSame = true;
                }
                if (!hasSame) {
                    return result;
                }
            }
        }

        return result;
    }
}
