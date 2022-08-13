package apple;

import java.util.ArrayList;
import java.util.List;

public class RemoveComments {
    public List<String> removeComments(String[] source) {
        List<String> result = new ArrayList<>();
        boolean inBlock = false;
        StringBuilder sb = new StringBuilder();
        for (String str : source) {
            for (int i = 0; i < str.length(); i++) {
                if (inBlock) {
                    if (str.charAt(i) == '*' && i < str.length() - 1 && str.charAt(i + 1) == '/') {
                        inBlock = false;
                        i++;
                    }
                } else {
                    if (str.charAt(i) == '/' && i < str.length() - 1 && str.charAt(i + 1) == '*') {
                        inBlock = true;
                        i++;
                    } else if (str.charAt(i) == '/' && i < str.length() - 1 && str.charAt(i + 1) == '/') {
                        break;
                    } else {
                        sb.append(str.charAt(i));
                    }
                }
            }
            if (!inBlock && sb.length() > 0) {
                result.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        return result;
    }

    public static List<String> removeComments2(String[] source) {
        List<String> result = new ArrayList<>();
        boolean inBlock = false;
        boolean inString = false;
        StringBuilder sb = new StringBuilder();
        for (String str : source) {
            for (int i = 0; i < str.length(); i++) {
                if (inBlock) {
                    if (str.charAt(i) == '*' && i < str.length() - 1 && str.charAt(i + 1) == '/') {
                        inBlock = false;
                        i++;
                    }
                } else if (inString) {
                    if (str.charAt(i) == '"') {
                        inString = false;
                    }
                    sb.append(str.charAt(i));
                } else {
                    if (str.charAt(i) == '/' && i < str.length() - 1 && str.charAt(i + 1) == '*') {
                        inBlock = true;
                        i++;
                    } else if (str.charAt(i) == '/' && i < str.length() - 1 && str.charAt(i + 1) == '/') {
                        break;
                    }  else {
                        if (str.charAt(i) == '"') {
                            inString = true;
                        }
                        sb.append(str.charAt(i));
                    }
                }
            }
            if (!inBlock && sb.length() > 0) {
                result.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String[] source = {
                "/*\n",
                " \"String s = \"s\"\"\n ",
                " */",
                "public static void main(String[] args) {",
                "   String s = \"s'\\\\t /*dadas\";",
                "}"
        };
        for (String s : removeComments2(source))
            System.out.println(s);
    }
}
