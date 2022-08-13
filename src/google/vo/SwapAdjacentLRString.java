package google.vo;

public class SwapAdjacentLRString {
    public static boolean canTransform(String start, String end) {
        int i = 0, j = 0, n = start.length();
        while (i < n || j < n) {
            while (i < n && start.charAt(i) == 'X') {
                i++;
            }
            while (j < n && end.charAt(j) == 'X') {
                j++;
            }
            if (i == n) {
                return j == n;
            }
            if (j == n) {
                return i == n;
            }
            if (start.charAt(i) != end.charAt(j)) {
                return false;
            }
            if (start.charAt(i) == 'L') {
                if (j > i) {
                    return false;
                }
            }
            if (start.charAt(i) == 'R') {
                if (i > j) {
                    return false;
                }
            }
            i++;
            j++;
        }
        return true;
    }

    public static void main(String[] args) {
        String start = "RXXLRXRXL", end = "XRLXXRRLX";
        System.out.println(canTransform(start, end));
    }
}
