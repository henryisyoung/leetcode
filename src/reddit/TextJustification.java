package reddit;

import java.util.ArrayList;
import java.util.List;

public class TextJustification {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int n = words.length;
        int i = 0;
        while (i < n) {
            int sum = 0;
            int count = 0;
            while (i < n && sum < maxWidth) {
                if (sum + words[i].length() > maxWidth) break;
                sum = sum + 1 + words[i++].length();
                count++;
            }
            String s = "";
            if (i == n || count == 1) {
                for (int k = i - count; k < i; k++) {
                    s += words[k];
                    if (k == i - 1) break;
                    s += " ";
                }
                while (s.length() < maxWidth) {
                    s += " ";
                }
            } else {
                int avg = (maxWidth - (sum - 1)) / (count - 1);
                int reminder = (maxWidth - (sum - 1)) % (count - 1);
                for (int k = i - count; k < i; k++) {
                    s += words[k];
                    if (k == i - 1) break;
                    for (int t = 0; t <= avg; t++) {
                        s += " ";
                    }
                    if (k < i - count + reminder) {
                        s += " ";
                    }
                }
            }
            result.add(s);
        }

        return result;
    }
}
