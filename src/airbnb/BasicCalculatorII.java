package airbnb;

public class BasicCalculatorII {
    public static int calculate(String s) {
        int md=-1; // 0 is m, 1 is d
        int sign=1; // 1 is +, -1 is -
        int prev=0;
        int result=0;

        for(int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if(Character.isDigit(c)){
                int num = c-'0';
                while(++i<s.length() && Character.isDigit(s.charAt(i))){
                    num = num*10+s.charAt(i)-'0';
                }
                i--; // back to last digit of number

                if(md==0){
                    prev = prev * num;
                    md=-1;
                }else if(md==1){
                    prev = prev / num;
                    md=-1;
                }else{
                    prev = num;
                }
            }else if(c=='/'){
                md=1;
            }else if(c=='*'){
                md=0;
            }else if(c=='+'){
                result = result + sign*prev;
                sign=1;
            }else if(c=='-'){
                result = result + sign*prev;
                sign=-1;
            }
        }

        result = result + sign*prev;
        return result;
    }

    public static void main(String[] args) {
        int result = calculate("1*2*13");
        System.out.println(result);
    }
}
