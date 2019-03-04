package airbnb;

import java.util.ArrayList;
import java.util.List;

public class CSVparser {
    public static String parseCSV(String str) {
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        boolean isQuoted = false;
        int n = str.length();

        for (int i = 0; i < n; i++) {
            if (isQuoted) {
                if (str.charAt(i) == '\"') {
                    if (i + 1 < n && str.charAt(i) == str.charAt(i + 1)) {
                        sb.append(str.charAt(i));
                        i++;
                    } else {
                        isQuoted = false;
                    }
                } else {
                    sb.append(str.charAt(i));
                }
            } else {
                if (str.charAt(i) == '\"') {
                    isQuoted = true;
                } else if (str.charAt(i) == ',') {
                    result.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(str.charAt(i));
                }
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
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
