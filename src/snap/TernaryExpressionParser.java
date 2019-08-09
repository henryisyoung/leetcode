package snap;

public class TernaryExpressionParser {
    public static String parseTernary(String expression) {
        if (expression == null || expression.length() == 0) return "";
        if(expression.length()==1) return expression;

        char c = expression.charAt(0);
        int count = 1, i;
        for (i = 2; i < expression.length(); i++) {
            if (expression.charAt(i) == '?') count++;
            if (expression.charAt(i) == ':') count--;
            if (count == 0) break;
        }
        return c == 'T' ? parseTernary(expression.substring(2, i)) : parseTernary(expression.substring(i + 1));
    }

    public static void main(String[] args) {
        String expression = "F?1:T?4:5";
        System.out.println(parseTernary(expression));
    }
}
