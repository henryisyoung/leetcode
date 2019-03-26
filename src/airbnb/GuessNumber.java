package airbnb;

public class GuessNumber {

    public static void main(String args[]) {
        String[] words = {"4323", "1354", "2222", "1231", "5432", "1111", "3233"};
        for (String word : words) {
            System.out.println(client(word).equals(word));
        }
    }

    public static String client(String t){
        char[] result = {'6','6','6','6'};
        String base = "1111";
        int baseCount = check(base, t);
        if (baseCount == 4) {
            return base;
        }
        for (int i = 0; i < 4; i++) {
            for (char c = '2'; c < '6'; c++) {
                String newBase = replace(i, c, base);
                int newBaseCount = check(newBase, t);
                if (newBaseCount != baseCount) {
                    result[i] = baseCount > newBaseCount ? '1' : c;
                }
            }
        }
        return new String(result);
    }

    private static String replace(int i, char c, String base) {
        char[] arr = base.toCharArray();
        arr[i] = c;
        return new String(arr);
    }

    private static int check(String base, String t) {
        int count = 0;
        for (int i = 0; i < base.length(); i++) {
            if (base.charAt(i) == t.charAt(i)) {
                count++;
            }
        }
        return count;
    }
}
