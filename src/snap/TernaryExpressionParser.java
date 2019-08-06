package snap;

public class TernaryExpressionParser {
    public String parseTernary(String expression) {
        if(expression==null || expression.length()==0) return "";
        if(expression.length()==1) return expression;
        boolean cond = expression.charAt(0)=='T';

        int count = 1;
        int i = 2;
        while(count != 0){
            char curChar = expression.charAt(i);
            if(curChar=='?') count++;
            else if(curChar==':') count--;
            i++;
        }
        return cond ? parseTernary(expression.substring(2,i-1)) : parseTernary(expression.substring(i,expression.length()));
    }
}
