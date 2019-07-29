package uber;

public class CustomSortString {
    public String customSortString(String S, String T) {
        if (S == null || S.length() == 0) return T;
        int[] table = new int[256];
        for (char c : T.toCharArray()) {
            table[c]++;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : S.toCharArray()) {
            if (table[c] != 0) {
                while (table[c] > 0) {
                    table[c]--;
                    sb.append(c);
                }
            }
        }
        for (int i = 0; i < 256; i++) {
            while (table[i] != 0) {
                table[i]--;
                sb.append((char) i);
            }
        }
        return sb.toString();
    }
}
