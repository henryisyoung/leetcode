package facebook;

public class AddBinary {
    public static String addBinary(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int carry = 0;
        int i = a.length() - 1, j = b.length() - 1;
        while (i >= 0 || j >= 0) {
            if (i >= 0) {
                carry += a.charAt(i--) == '1' ? 1 : 0;
            }
            if (j >= 0) {
                carry += b.charAt(j--) == '1' ? 1 : 0;
            }
            sb.append(carry % 2);
            carry /= 2;
        }
        if (carry != 0) sb.append(carry);
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        System.out.println(addBinary("1010", "1011"));
    }
}
