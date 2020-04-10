package facebook;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GoatLatin {
    public static String toGoatLatin(String S) {
        String[] strs = S.split(" ");
        StringBuilder sb = new StringBuilder();
        Set<Character> set = new HashSet<>();
        set.addAll(Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'));

        for (int i = 0; i < strs.length; i++) {
            char c = strs[i].charAt(0);
            String cur;
            if (set.contains(c)) {
                cur = strs[i] + "ma";
            } else {
                cur = strs[i].substring(1) + c + "ma";
            }
            int count = i + 1;
            while (count > 0) {
                count--;
                cur += 'a';
            }
            if (i == strs.length - 1){
                sb.append(cur);
            } else {
                sb.append(cur + " ");
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(toGoatLatin("I speak Goat Latin").equals("Imaa peaksmaaa oatGmaaaa atinLmaaaaa"));
        System.out.println(toGoatLatin("The quick brown fox jumped over the lazy dog").equals("heTmaa uickqmaaa rownbmaaaa oxfmaaaaa umpedjmaaaaaa overmaaaaaaa hetmaaaaaaaa azylmaaaaaaaaa ogdmaaaaaaaaaa"));
    }
}
