package Amplitude;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DictionaryCoding {
    /*
    https://agprashant.medium.com/dictionary-encoding-java-3d30a465233e
    例如给[apple, orange, melon, apple] 返回string "apple, orange, melon:0,1,2,0"。
    返回"orange, melon, apple:2,0,1,2"也是有效的
    decoding: 给一个string "apple, orange, melon:0,1,2,0"，返回[apple, orange, melon, apple]
     */

    public static String encode(final String input) {
        String[] words = input.split(",");
        Map<String, Integer> wordIdMap = new HashMap<>();
        Map<Integer, String> idWordMap = new LinkedHashMap<>();
        int id = 0;
        for (String word : words) {
            if (!wordIdMap.containsKey(word)) {
                wordIdMap.put(word, id);
                idWordMap.put(id, word);
                id++;
            }
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String word : idWordMap.values()) {
            sb.append(word);
            if (i++ == wordIdMap.size() - 1) {
                break;
            }
            sb.append(",");
        }
        sb.append(":");
        i = 0;
        for (String word : words) {
            sb.append(wordIdMap.get(word));
            if (i++ == words.length - 1) {
                break;
            }
            sb.append(",");
        }
        return sb.toString();
    }

    public static String decode(final String input) {
        String[] array = input.split(":");
        String[] words = array[0].split(",");
        String[] ids = array[1].split(",");
        int len = ids.length;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int id = Integer.parseInt(ids[i]);
            sb.append(words[id]);
            if (id == len - 1) {
                break;
            }
            sb.append(",");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        String input = "apple,orange,melon,apple";
//        String input2 = "USA,USA,USA,USA,Mexico,Canada,Mexico,Mexico,Mexico,Argentina";
//        System.out.println(encode(input2));


        System.out.println(decode("USA,Mexico,Canada,Argentina:0,0,0,0,1,2,1,1,1,3"));
    }
}
