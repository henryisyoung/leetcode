package pinterest;

import java.util.*;

public class ExpressCalculator {
    public String expresscalculator(String input, Map<String,Integer> map) {
        if(input == null || input.length()==0) {
            return "";
        }
        String simple = simplify(input,map);
        int i = 0;
        Deque<cell> stack = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        int num = 0;
        int sum=0;
        int sign = 1;
        while(i<simple.length()){
            if(isdigit(simple.charAt(i))) {
                num=num*10+simple.charAt(i)-'0';
                i++;
            }else if (simple.charAt(i)=='+'||simple.charAt(i)=='-'){
                sum=sum+num*sign;
                sign=simple.charAt(i)=='+'?1:-1;
                num=0;
                i++;
            }else if (ischar(simple.charAt(i))){
                if(sign==1){
                    sb.append('+');
                }else{
                    sb.append('-');
                }
                while(i<simple.length()&&ischar(simple.charAt(i))){
                    sb.append(simple.charAt(i));
                    i++;
                }
            }else if (simple.charAt(i)=='(') {
                stack.offerFirst(new cell(sum,sign,sb.toString()));
                sum=0;
                sign=1;
                sb = new StringBuilder();
                i++;
            }else if (simple.charAt(i)==')') {
                cell prev = stack.pollFirst();
                sum=sum+sign*num;
                int t = prev.sign*sum;
                num=0;
                sum=prev.sum+t;
                StringBuilder next = new StringBuilder();
                next.append(prev.str);
                if(sb.length()>0){
                    next.append(update(prev.sign,sb.toString()));
                }
                sb = next;
                sign=1;
                i++;
            }
        }
        if(num!=0){
            sum=sum+sign*num;
        }
        sb.append('+');
        sb.append(sum);
        return sb.toString();
    }

    private String update(int sign, String s){
        StringBuilder sb = new StringBuilder();
        if(sign==1){
            return s;
        }else{
            for(int i=0; i<s.length();i++){
                if(s.charAt(i)=='+'){
                    sb.append('-');
                }else if (s.charAt(i)=='-'){
                    sb.append('+');
                }else{
                    sb.append(s.charAt(i));
                }
            }
        }
        return sb.toString();
    }

    class cell{
        int sum;
        int sign;
        String str;
        public cell(int s, int sign, String str){
            this.sum=s;
            this.sign=sign;
            this.str=str;
        }
    }

    private String simplify(String input, Map<String,Integer> map) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < input.length()){
            if(ischar(input.charAt(i))) {
                int fast = i;
                StringBuilder temp = new StringBuilder();
                while(fast<input.length() && ischar(input.charAt(fast))){
                    temp.append(input.charAt(fast));
                    fast++;
                }
                String s = temp.toString();
                if(map.containsKey(s)){
                    sb.append(map.get(s));
                    i=fast;
                }else{
                    sb.append(s);
                    i=fast;
                }
            }else{
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }
    private boolean ischar(char c){
        return c>='a' && c<='z';
    }
    private boolean isdigit(char c){
        return c>='0' && c<='9';
    }

}
