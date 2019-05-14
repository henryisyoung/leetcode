package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class RestoreIPAddresses {
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        if(s== null || s.length() < 4 || s.length() > 16){
            return result;
        }
        dfsSearchAll(result, new ArrayList<String>(), 0, s);
        return result;
    }

    private void dfsSearchAll(List<String> result, List<String> cur, int pos, String s) {
        if (cur.size() == 4 && pos == s.length()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    sb.append(cur.get(i));
                } else {
                    sb.append("." + cur.get(i));
                }
            }
            result.add(sb.toString());
            return;
        }
        for (int i = pos + 1; i <= pos + 3 && i <= s.length(); i++) {
            String ip = s.substring(pos, i);
            if (isValidIP(ip)) {
                cur.add(ip);
                dfsSearchAll(result, cur, i, s);
                cur.remove(cur.size() - 1);
            }
        }
    }

    private boolean isValidIP(String ip) {
        if (ip.charAt(0) == '0') {
            return ip.equals("0");
        }
        return Integer.parseInt(ip) < 256;
    }

    public static void main(String[] args) {
        String s = "25525511135";
        RestoreIPAddresses solver = new RestoreIPAddresses();
        List<String> result = solver.restoreIpAddresses(s);
        System.out.println(result.toString());
    }
}
