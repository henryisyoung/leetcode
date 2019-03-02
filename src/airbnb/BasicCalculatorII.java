package airbnb;

public class BasicCalculatorII {
    public static int calculate(String s) {
        int result = 0, prev = 0;
        int sign = 1, mdSign = -1;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                int num = 0;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + s.charAt(i) - '0';
                    i++;
                }
                i--;
                if (mdSign == 0) { // multiply
                    prev *= num;
                    mdSign = -1;
                } else if (mdSign == 1) {
                    // divide
                    prev /= num;
                    mdSign = -1;
                } else {
                    prev = num;
                }
            } else if (s.charAt(i) == '*') {
                mdSign = 0;
            } else if (s.charAt(i) == '/') {
                mdSign = 1;
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
        int result = calculate("1*2*13");
        System.out.println(result);
    }
}
