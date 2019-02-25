package airbnb;

import java.util.*;

public class PyramidTransitionMatrix {
    public boolean pyramidTransition(String bottom, List<String> allowed) {
        if (bottom == null || bottom.length() == 0) {
            return false;
        }
        Map<String, List<String>> map = new HashMap<>();
        for (String str : allowed) {
            String base = str.substring(0, 2);
            String above = str.substring(2);
            if (map.containsKey(base)) {
                map.get(base).add(above);
            } else {
                List<String> list = new ArrayList<>();
                list.add(above);
                map.put(base, list);
            }
        }

        return dfsBuilder(map, bottom, "", 0);
    }

    private boolean dfsBuilder(Map<String, List<String>> map, String bottom, String above, int pos) {
        if (above.length() == 1 && bottom.length() == 2) {
            return true;
        }
        if (bottom.length() - 1 == above.length()) {
            return dfsBuilder(map, above, "", 0);
        }
        String cur = bottom.substring(pos, pos + 2);
        if (map.containsKey(cur)) {
            List<String> list = map.get(cur);
            for (String top : list) {
                if (dfsBuilder(map, bottom, above + top, pos + 1)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean pyramidTransitionDP(String bottom, List<String> allowed) {
        int n = bottom.length();
        boolean[][][] dp = new boolean[n][n][7]; // Letters in all strings will be chosen from the set {'A', 'B', 'C', 'D', 'E', 'F', 'G'}
        Map<Character, Set<String>> map = new HashMap<>();
        for (String word : allowed) {
            String base = word.substring(0, 2);
            char above = word.charAt(2);
            if (map.containsKey(above)) {
                map.get(above).add(base);
            } else {
                Set<String> set = new HashSet<>();
                set.add(base);
                map.put(above, set);
            }
        }

        for (int i = 0; i < n; i++) {
            dp[n - 1][i][bottom.charAt(i) - 'A'] = true;
        }

        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                for (char c = 'A'; c <= 'Z'; c++) {
                    if (!map.containsKey(c)) {
                        continue;
                    }
                    for (String base : map.get(c)) {
                        char left = base.charAt(0), right = base.charAt(1);
                        if (dp[i + 1][j][left - 'A'] && dp[i + 1][j + 1][right - 'A']) {
                            dp[i][j][c - 'A'] = true;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            if (dp[0][0][i]) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String bottom = "XYZ", bottom2 = "XXYX", bottom3 = "CBDDA";
        List<String> allowed = Arrays.asList("XYD", "YZE", "DEA", "FFF");
        List<String> allowed2 = Arrays.asList("XXX", "XXY", "XYX", "XYY", "YXZ");
        List<String> allowed3 = Arrays.asList("ACC","ACA","AAB","BCA","BCB","BAC","BAA","CAC","BDA","CAA","CCA","CCC","CCB","DAD","CCD","DAB","ACD","DCA","CAD","CBB","ABB","ABC","ABD","BDB","BBC","BBA","DDA","CDD","CBC","CBA","CDA","DBA","ABA");
        PyramidTransitionMatrix solver = new PyramidTransitionMatrix();
//        System.out.println(solver.pyramidTransition(bottom, allowed));
//        System.out.println(solver.pyramidTransition(bottom2, allowed2));
//        System.out.println(solver.pyramidTransition(bottom3, allowed3));
        System.out.println(solver.pyramidTransitionDP(bottom3, allowed3));
    }
}
