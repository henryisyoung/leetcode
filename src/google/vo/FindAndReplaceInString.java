package google.vo;

import java.util.HashMap;
import java.util.Map;

public class FindAndReplaceInString {
    public static String findReplaceString(String s, int[] indices, String[] sources, String[] targets) {
        int i = 0, n = s.length();
        StringBuilder sb = new StringBuilder();
        Map<Integer, Integer> map = new HashMap<>();
        int pos = 0;
        for (int index : indices)
            map.put(index, pos++);

        while (i < n) {
            if (map.containsKey(i)) {
                int index = map.get(i);

                String src = sources[index], tar = targets[index];
                if (i + src.length() <= n && s.startsWith(src, i)) {
                    sb.append(tar);
                    i += src.length();
                } else {
                    sb.append(s.charAt(i++));
                }
            } else {
                sb.append(s.charAt(i++));
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
//        String s = "abcd";
//        int[] indices = {0, 2};
//        String[] sources = {"a", "cd"}, targets = {"eee", "ffff"};

        String s = "abcd";
        int[] indices = {0, 2};
        String[] sources = {"ab","ec"}, targets = {"eee", "ffff"};
        System.out.println(findReplaceString(s, indices, sources, targets));
    }
}
