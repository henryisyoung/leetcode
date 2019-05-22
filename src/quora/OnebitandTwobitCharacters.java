package quora;

public class OnebitandTwobitCharacters {
    public boolean isOneBitCharacter(int[] bits) {
        int i = 0, n = bits.length;
        while (i < n - 1) {
            if (bits[i] == 0) {
                i++;
            } else {
                i += 2;
            }
        }
        return i == n - 1;
    }

    public boolean isOneBitCharacter2(int[] bits) {
        if (bits == null || bits.length == 0) {
            return false;
        }
        int i = 0, n = bits.length;
        while (i < n - 1) {
            i += bits[i] + 1;
        }
        return i == n - 1;
    }
}
