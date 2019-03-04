package airbnb;

public class BasicCalculatorII {
    public static int calculate(String s) {
        int result = 0, prev = 0;
        if (s == null || s.length() == 0) {
            return result;
        }
        int n = s.length();
        int md = -1, sign = 1;

        for (int i = 0; i < n; i++) {
            if (Character.isDigit(s.charAt(i))) {
                int num = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    num = 10 * num + s.charAt(i) - '0';
                    i++;
                }
                i--;
                if (md == 0) {
                    // multiply
                    prev *= num;
                    md = -1;
                } else if (md == 1) {
                    // divide
                    prev /= num;
                    md = -1;
                } else {
                    prev = num;
                }
            } else if (s.charAt(i) == '*') {
                md = 0;
            } else if (s.charAt(i) == '/') {
                md = 1;
            } else if (s.charAt(i) == '+') {
                result = result + prev * sign;
                sign = 1;
            } else if (s.charAt(i) == '-') {
                result = result + prev * sign;
                sign = -1;
            }
        }
        result = result + prev * sign;
        return result;
    }

    public static void main(String[] args) {
        int result = calculate("10*2 - 13/10 - 1 + 8/3");
        System.out.println(result);
    }
}
