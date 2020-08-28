package contest;

public class ThousandSeparator {
    public static String thousandSeparator(int n) {
        if(n < 1000) {
            return String.valueOf(n);
        }
        int val = n % 1000;
        return thousandSeparator(n / 1000) + "." + parse(String.valueOf(val));
    }

    private static String parse(String s) {
        int rest = 3 - s.length();
        String result = s;
        while(rest > 0) {
            result = "0" + result;
            rest--;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(thousandSeparator(222));
    }
}
