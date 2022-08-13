package Amplitude;

import java.util.HashMap;
import java.util.Map;

public class DictionaryCoding2 {
    /*
    https://agprashant.medium.com/dictionary-encoding-java-3d30a465233e
    例如给[apple, orange, melon, apple] 返回string "apple, orange, melon:0,1,2,0"。
    返回"orange, melon, apple:2,0,1,2"也是有效的
    decoding: 给一个string "apple, orange, melon:0,1,2,0"，返回[apple, orange, melon, apple]
     */

    public static String encode(final String input) {
        StringBuilder sbWord = new StringBuilder();
        StringBuilder sbId = new StringBuilder();

        Map<String, Integer> map = new HashMap<>();
        String[] words = input.split(",");
        int id = 0;
        String prefix = "";
        for (String word : words) {
            if (map.containsKey(word)) {
                int wordId = map.get(word);
                sbId.append(prefix + wordId);
            } else {
                map.put(word, id);
                sbId.append(prefix + id);
                sbWord.append(prefix + word);
                id++;
            }
            prefix = ",";
        }

        return sbWord + ":" + sbId;
    }

    public static String decode(final String input) {
        String[] array = input.split(":");
        if (array == null || array.length != 2) {
            return "";
        }
        String[] words = array[0].split(",");
        String[] index = array[1].split(",");
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (int i = 0; i < index.length; i++) {
            int id = Integer.parseInt(index[i]);
            sb.append(prefix + words[id]);
            prefix = ",";
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        String input = "apple,orange,melon,apple";
        String input2 = "USA,USA,USA,USA,Mexico,Canada,Mexico,Mexico,Mexico,Argentina"; // “USA,Mexico,Canada,Argentina:0,0,0,0,1,2,1,1,1,3”
        System.out.println(encode(input2));

        System.out.println(decode(encode(input2)).equals(input2));
    }
}
