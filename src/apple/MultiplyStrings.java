package apple;

public class MultiplyStrings {
    public String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] result = new int[m + n];
        for (int i = m - 1; i >= 0; i--) {
            int a = num1.charAt(i) - '0';
            for (int j = n - 1; j >= 0; j--) {
                int b = num2.charAt(j) - '0';
                int val = a * b + result[i + j + 1];
                result[i + j + 1] += val % 10;
                result[i + j] += val / 10;
            }
        }
        int pos = result[0] == 0 ? 1 : 0;
        StringBuilder sb = new StringBuilder();
        for (; pos < m + n; pos++) {
            sb.append(result[pos]);
        }
        return sb.toString();
    }
}
