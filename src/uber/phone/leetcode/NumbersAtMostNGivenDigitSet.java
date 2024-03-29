package uber.phone.leetcode;

public class NumbersAtMostNGivenDigitSet {
    // https://leetcode.com/problems/numbers-at-most-n-given-digit-set/
    // https://www.cnblogs.com/grandyang/p/11062193.html
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
                } else {
                    break;
                }
            }
            if (!hasSame) {
                return result;
            }
        }

        return result;
    }
}
