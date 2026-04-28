package LinkedIn.phone;

public class CanPlaceFlowers {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        if (flowerbed == null || flowerbed.length < n) {
            return false;
        }
        int count  = 0;
        for (int  i = 0; i < flowerbed.length; i++) {
            if (flowerbed[i] == 1) {
                if (i > 0) flowerbed[i - 1]= -1;
                if (i < flowerbed.length - 1) flowerbed[i + 1]= -1;
            }
        }
        for (int i = 0; i < flowerbed.length; i++) {
            if (flowerbed[i] == 0) {
                count++;
                if (i < flowerbed.length - 1) flowerbed[i + 1]= -1;
            }
        }

        return count >= n;
    }
}
