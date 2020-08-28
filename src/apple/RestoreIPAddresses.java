package apple;

import java.util.ArrayList;
import java.util.List;

public class RestoreIPAddresses {
    public static List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.length() < 4 || s.length() > 12) return result;
        findAll(result, 0, s, new ArrayList<>());
        return result;
    }

    private static void findAll(List<String> result, int pos, String s, List<String> ips) {
        if (pos == s.length() && ips.size() == 4) {
            result.add(buildIPS(ips));
            return;
        }
        for (int i = pos + 1; i <= s.length() && i <= pos + 3; i++) {
            String str = s.substring(pos, i);
            if (str.length() > 1 && str.charAt(0) == '0') break;
            if (Integer.parseInt(str) < 256) {
                ips.add(str);
                findAll(result, i, s, ips);
                ips.remove(ips.size() - 1);
            }
        }
    }

    private static String buildIPS(List<String> ips) {
        StringBuilder sb = new StringBuilder();
        sb.append(ips.get(0));
        for (int i = 1; i < ips.size(); i++) sb.append("." + ips.get(i));
        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "25525511135";
        System.out.println(restoreIpAddresses(s));
    }
}
