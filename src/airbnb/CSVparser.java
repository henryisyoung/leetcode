package airbnb;

import java.util.ArrayList;
import java.util.List;

public class CSVparser {
    public String parseCSV(String str) {
        List<String> res = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (inQuote) {
                if (str.charAt(i) == '\"') {
                    if (i < str.length() - 1 && str.charAt(i + 1) == '\"') {
                        sb.append("\"");
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
                    res.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(str.charAt(i));
                }
            }
        }
        if (sb.length() > 0) {
            res.add(sb.toString());
        }
        return res.toString();
    }
}
