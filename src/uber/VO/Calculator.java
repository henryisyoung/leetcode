package uber.VO;

import java.util.Stack;

public class Calculator {
    /*
    // 1. Parse string计算Add(Add(1, 2), Sub(3, 4))
    // https://www.1point3acres.com/bbs/thread-895738-1-1.html

    https://www.1point3acres.com/bbs/thread-889626-1-1.html
    第二题有点类似漆伞柳，只需要实现加减，格式是add(elem1,elem2) sub(elem1,elem2)外加嵌套，但是困难的点在于说明了输入不一定valid所以需要检查，
    比如可能出现一个单输入的操作或者三输入的操作，甚至“add“这三个字母都可能有typo，这题我看了就很头大，因为既要string parse又要排错，
    想到了要用stack但一直纠结是stack存运算还是数字，折腾了很久几乎暴力法做出来后面试官给了提示说可以stack同时存运算和数字，
    这样可以直接检查输入格式是否正确（关于string里括号和逗号位置的检查也需要提，但应该不重要），到最后好像也只是理清了思路没写完特别完备的代码
    */

    public static Integer calculator(String cals) {
        Stack<Character> symbols = new Stack<>();
        Stack<Integer> vals = new Stack<>();

        StringBuilder sb = new StringBuilder();
        cals = cals.replaceAll(" ", "");
        System.out.println(cals);
        int i = 0, n = cals.length();

        while (i < n) {
            char c = cals.charAt(i);
            if (Character.isDigit(c)) {
                int val = 0;
                while (i < n && Character.isDigit(cals.charAt(i))) {
                    val = 10 * val + cals.charAt(i) - '0';
                    i++;
                }
                vals.add(val);
            } else if (Character.isLetter(c)) {
                sb.append(c);
                i++;
            } else if (c == '(') {
                String str = sb.toString();
                sb = new StringBuilder();
                if (str.equals("Add")) {
                    symbols.add('+');
                } else if (str.equals("Sub")) {
                    symbols.add('-');
                } else {
                    throw new IllegalStateException("Not a valid input");
                }
                i++;
            } else if (c == ')') {
                if (symbols.isEmpty()) {
                    throw new IllegalStateException("Not a valid input");
                }
                char operation = symbols.pop();
                if (vals.size() < 2) {
                    throw new IllegalStateException("Not a valid input");
                }
                int val1 = vals.pop(), val2 = vals.pop();
                if (operation == '+') {
                    vals.add(val1 + val2);
                } else {
                    vals.add(val2 - val1);
                }
                i++;
            } else {
                i++;
            }
        }
        if (vals.size() != 1 && !symbols.empty()) {
            throw new IllegalStateException("Not a valid input");
        }
        return vals.peek();
    }

    public static void main(String[] args) {
        String str = "Add(Add( Add(1, 2), Add (1, 2)), Sub (Add(1, 2),Add(1, 2)))";
        System.out.println(calculator(str));
    }
}
