package airbnb;

import java.util.ArrayList;
import java.util.List;

public class TextJustification {
    public static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        if (words == null || words.length == 0) return result;
        int i = 0, n = words.length;
        while (i < n) {
            int sum = 0, count = 0;
            while (i < n && sum < maxWidth) {
                if (sum + words[i].length() > maxWidth) break;
                sum = sum + 1 + words[i].length();
                i++;
                count++;
            }
            String str = "";
            if (count == 1 || i == n) {
                for (int k = i - count; k < i; k++) {
                    str += words[k];
                    if (k == i - 1) break;
                    str += " ";
                }
                while (str.length() < maxWidth){
                    str += " ";
                }
            } else {
                int ave = (maxWidth - sum + 1) / (count - 1);
                int reminder = (maxWidth - sum + 1) % (count - 1);
                for (int k = i - count; k < i; k++) {
                    str += words[k];
                    if (k == i - 1) break;
                    for (int t = 0; t <= ave; t++) {
                        str += " ";
                    }
                    if (k < i - count + reminder) {
                        str += " ";
                    }
                }
            }
            result.add(str);
        }
        return result;
    }

    public static void main(String[] args) {
        String[] words = {"abcd", "efgh", "gsdd", "iuyt"};
        int maxWidth = 9;
        List<String> list = fullJustify(words, maxWidth);
        System.out.println(list.toString());
    }
}
