package facebook;

public class MultiplyStrings {
    public static String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] result = new int[m + n];

        for (int i = m - 1; i >= 0; i--) {
            int a = num1.charAt(i) - '0';
            for (int j = n - 1; j >= 0; j--) {
                int b = num2.charAt(j) - '0';
                int val = a * b + result[i + j + 1];
                result[i + j + 1] = val % 10;
                result[i + j] += val / 10;
            }
        }
        int start = result[0] == 0 ? 1 : 0;
        StringBuilder sb = new StringBuilder();
        for (;start < result.length; start++) {
            sb.append(result[start]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        System.out.println(multiply("123","456"));
        int a = 123345;
    }
}
