package airbnb;

import java.util.ArrayList;
import java.util.List;

public class CSVparser {
    public static String parseCSV(String str) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;

        for (int i = 0; i < str.length(); i++) {
            if (inQuote) {
                if (str.charAt(i) == '\"') {
                    if (i < str.length() - 1 && str.charAt(i) == '\"' && str.charAt(i + 1) == '\"') {
                        sb.append('\"');
                        i++;
                    } else {
                        inQuote = false;
                    }
                } else {
                    sb.append(str.charAt(i));
                }
            } else {
                if (str.charAt(i) == '\"') {
                    inQuote = true;
                } else if (str.charAt(i) == ',') {
                    result.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(str.charAt(i));
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String s = "\"Alexandra \"\"Alex\"\"\",Menendez,alex.menendez@gmail.com,Miami,1 \"\"\"Alexandra Alex\"\"\"";
        String s2 = "John,Smith,john.smith@gmail.com,Los Angeles,1";
        String s3 = "Jane,Roberts,janer@msn.com,\"San Francisco, CA\",0";
        System.out.println(parseCSV(s));
    }
}
