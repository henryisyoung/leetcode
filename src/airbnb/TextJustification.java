package airbnb;

import java.util.ArrayList;
import java.util.List;

public class TextJustification {
    public static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }
        int n = words.length;
        int i = 0;
        while (i < n) {
            int count= 0, sum = 0;
            String str = "";
            while (i < n && sum <= maxWidth + 1) {
                if (sum + words[i].length() > maxWidth) {
                    break;
                } else {
                    sum = sum + 1 + words[i].length();
                    count++;
                    i++;
                }
            }

            if(count == 1 || i == words.length){
                for(int k = i - count; k < i; k++){
                    str += words[k];
                    if(k == i - 1){
                        break;
                    } // last word
                    str += " ";
                }
                while(str.length() < maxWidth){
                    str += " ";
                }
                //middle justification
            } else {
                int aveSpace = (maxWidth + 1 - sum) / (count - 1);
                int reminder = (maxWidth + 1 - sum) % (count - 1);

                for (int k = i - count; k < i; k++) {
                    str += words[k];
                    if (k == i - 1) {
                        break;
                    }
                    for (int s = 0; s <= aveSpace; s++ ){
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
