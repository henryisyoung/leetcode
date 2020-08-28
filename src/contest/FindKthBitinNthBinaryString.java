package contest;

public class FindKthBitinNthBinaryString {
    public static char findKthBit(int n, int k) {
        if (n == 1) return '0';
        if (n == 2) return "011".charAt(k - 1);
        int len = (int) (Math.pow(2, n) - 1);
        if (k - 1 == len / 2) return '1';
        if (k - 1 < len / 2) return findKthBit(n - 1, k);
        char t = findKthBit(n - 1, len - (k - 1));
        return t == '1' ? '0' : '1';
    }

    public static void main(String[] args) {
        int n = 4, k = 11;
        System.out.println(findKthBit(n, k));
        System.out.println((int) Math.pow(2,3)-1);
    }

}
