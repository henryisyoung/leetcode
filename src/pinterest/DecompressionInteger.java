package pinterest;

import java.util.*;

public class DecompressionInteger {
    public List<String> decompression(String s) {
        List<String> result  = new ArrayList<>();
        if (s == null || s.length() == 0) {
            return result;
        }
        if (s.length() == 1) {
            result.add(s);
            return result;
        }
        Map<String, List<String>> map = new HashMap<>();
        return dfsSarchAll(s, 0, map);
    }

    private List<String> dfsSarchAll(String s, int start, Map<String, List<String>> map) {
        if (map.containsKey(s.substring(start))) {
            return map.get(s.substring(start));
        }
        if (s.length() - start == 2) {
            List<String> result  = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            int count = s.charAt(start) - '0', val = s.charAt(start + 1) - '0';
            while (count > 0) {
                count--;
                sb.append(val);
            }
            String str = s.substring(start);
            map.put(str, new ArrayList<>());
            map.get(str).add(sb.toString());
            result.add(sb.toString());
            return result;
        }
        List<String> result  = new ArrayList<>();
        for (int end = start + 2; end <= s.length() - 2; end++) {
            String str = s.substring(start, end);
            List<String> nextList = dfsSarchAll(s, end, map);
            List<String> list = findAll(str);
//            if (map.containsKey(str)) {
//                list = map.get(str);
//            } else {
//                list = findAll(str);
//                map.put(str, list);
//            }
            for (String first : list) {
                for (String second : nextList) {
                    result.add(first + second);
                }
            }
            map.put(str, result);
        }
        return result;
    }

    private List<String> findAll(String s) {
        List<String> result  = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = Integer.parseInt(s.substring(0, s.length() - 1));
        int val = s.charAt(s.length() - 1) - '0';
        while (count > 0) {
            count--;
            sb.append(val);
        }
        result.add(sb.toString());
        return result;
    }

    public static void main(String[] args) {
        DecompressionInteger sovler = new DecompressionInteger();
        String s = "1322";
        List<String> result = sovler.decompression(s);
        System.out.println(result.toString());
    }
}
