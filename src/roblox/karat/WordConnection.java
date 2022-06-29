package roblox.karat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordConnection {
    public static List<String> connectWords(String[] words, int max) {
        List<String> result = new ArrayList<>();
        int i = 0, n = words.length;

        while (i < n) {
            int sum = 0;
            int count = 0;
            while (i < n && sum < max) {
                if (sum + words[i].length() > max) break;
                sum = sum + words[i++].length() + 1;
                count++;
            }

            String s = "";
            for (int k = i - count; k < i; k++) {
                s += words[k];
                if (k == i - 1) {
                    break;
                }
                s += "-";
            }
            result.add(s);
        }

        return result;
    }

    /*
    lines = [ "The day began as still as the",
          "night abruptly lighted with",
          "brilliant flame" ]

        reflowAndJustify(lines, 24) ... "reflow lines and justify to length 24" =>

        [ "The--day--began-as-still",
          "as--the--night--abruptly",
          "lighted--with--brilliant",
          "flame" ] // <--- a single word on a line is not padded with spaces
     */

    public static List<String> reflowAndJustify(String[] lines, int max) {
        List<String> list = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            String[] array = line.split(" ");
            list.addAll(Arrays.asList(array));
        }
        int n = list.size(), i = 0;

        while (i < n) {
            int sum = 0, count = 0;
            while (i < n && sum < max) {
                if (sum + list.get(i).length() > max) break;
                sum = sum + list.get(i++).length() + 1;
                count++;
            }
            String s = "";
            if (count == 1) {
                result.add(list.get(i - 1));
            } else {
                int avg = (max - (sum - 1)) / (count - 1);
                int reminder = (max - (sum - 1)) % (count - 1);
                for (int k = i - count; k < i; k++) {
                    s += list.get(k);
                    if(k == i - 1) break;
                    for (int t = 0; t <= avg; t++) {
                        s += "-";
                    }
                    if (k < i - count + reminder) {
                        s += "-";
                    }
                }
                result.add(s);
            }
        }

        return result;
    }



    public static void main(String[] args) {
//        String[] words = {"this", "is", "a", "good", "day", "do", "you"};
//        int max = 400;
//        System.out.println(connectWords(words, max));

        String[] lines = {"The day began as still as the",
                "night abruptly lighted with",
                "brilliant flame" };
        int max2 = 24;
        System.out.println(reflowAndJustify(lines, max2));
    }
}
