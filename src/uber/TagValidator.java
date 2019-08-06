package uber;

import java.util.Stack;

public class TagValidator {
    public boolean isValid(String code) {
        // Write your code here
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < code.length();) {
            if (i > 0 && stack.isEmpty()) {
                return false;
            }
            if (code.startsWith("<![CDATA[", i)) {    //出现"<![CDATA["
                int j = i + 9;
                i = code.indexOf("]]>", j);	//找到"]]>"
                if(i < 0) {
                    return false;
                }
                i += 3;
            }
            else if (code.startsWith("</", i)) {  //出现"</"
                int j = i + 2;
                i = code.indexOf('>', j);	//找到">"
                if (i < 0 || i == j || i - j > 9) {
                    return false;
                }
                for (int k = j; k < i; k++) {
                    if (!Character.isUpperCase(code.charAt(k))) {	//标签字母要求大写
                        return false;
                    }
                }
                String s = code.substring(j, i++);
                if (stack.isEmpty() || !stack.pop().equals(s)) {  //与之前存入的标签前段相比，是否匹配
                    return false;
                }
            }
            else if (code.startsWith("<", i)) {	//出现"<"
                int j = i + 1;
                i = code.indexOf('>', j);		 //找到">"
                if (i < 0 || i == j || i - j > 9) {	//如果标签长度超过9就错误
                    return false;
                }
                for (int k = j; k < i; k++) {
                    if (!Character.isUpperCase(code.charAt(k))) {  //标签字母要求大写
                        return false;
                    }
                }
                String s = code.substring(j, i++);	//截取标签内容
                stack.push(s);			//将标签内容存入列表中
            }
            else {
                i++;
            }
        }
        return stack.isEmpty();
    }
}
