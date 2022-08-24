package rippling.VO;

import java.util.ArrayList;
import java.util.List;

public class TextJustification {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0, n = words.length;
        while (i < n) {
            int count = 0;
            int sum = 0;
            while (i < n && sum < maxWidth) {
                if (sum + words[i].length() > maxWidth) {
                    break;
                }
                sum = sum + 1 + words[i].length();
                i++;
                count++;
            }
            StringBuilder sb = new StringBuilder();
            if (count == 1 || i == n) {
                for (int k = i - count; k < i; k++) {
                    sb.append(words[k]);
                    if (k < i - 1) {
                        sb.append(" ");
                    }
                }
                while (sb.length() < maxWidth) {
                    sb.append(" ");
                }
            } else {
                int avgSpace = (maxWidth - (sum - 1)) / (count - 1);
                int remSpace = (maxWidth - (sum - 1)) % (count - 1);

                for (int k = i - count; k < i; k++) {
                    sb.append(words[k]);
                    if (k < i - 1) {
                        for (int t = 0; t <= avgSpace; t++) {
                            sb.append(" ");
                        }
                        if (k < i - count + remSpace) {
                            sb.append(" ");
                        }
                    }
                }
            }
            result.add(sb.toString());
        }

        return result;
    }
}
