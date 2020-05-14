package facebook;

public class AddNumber {
    public static String addNumber(String a, String b) {
        String[] aArray = a.split("\\.");
        String[] bArray = b.split("\\.");

        int val = 0;
        StringBuilder sb = new StringBuilder();

        String l = bArray[1].length() > aArray[1].length() ? bArray[1] : aArray[1];
        String s = bArray[1].length() < aArray[1].length() ? bArray[1] : aArray[1];
        int min = s.length(), max = l.length();
        int i = min - 1, j = max - 1;
        while (j > i) {
            sb.append(l.charAt(j--));
        }
        while (i >= 0 && j >= 0) {
            val += l.charAt(j--) - '0' + s.charAt(i--) - '0';
            sb.append(val % 10);
            val /= 10;
        }
        String second = sb.reverse().toString();
        i = aArray[0].length() - 1;
        j = bArray[0].length() - 1;
        sb = new StringBuilder();
        while (i >= 0 || j >= 0) {
            if (i >= 0) {
                val += aArray[0].charAt(i--) - '0';
            }
            if (j >= 0) {
                val += bArray[0].charAt(j--) - '0';
            }
            sb.append(val % 10);
            val /= 10;
        }
        if (val != 0) sb.append(val);
        return sb.reverse().toString() + "." + second;
    }

    public static void main(String[] args) {
        String a = "123.123", b = "23.0";
        System.out.println(addNumber(a, b));
    }
}
