package uber.phone;

public class NextSmallestPalindrome {
    // https://www.geeksforgeeks.org/given-a-number-find-next-smallest-palindrome-larger-than-this-number/
    public static int nextSmallestPalindrome (int n) {
        int[] array = intToArray(n);
        int allNine = allNine(n);
        if (allNine != -1) {
            return allNine;
        }
        int[] newArray = mirrorLeft(array);
        int newNum = arrayToNumber(newArray);
        if (newNum > n) {
            return newNum;
        } else {
            int[] addMid = addMid(newArray);

            return arrayToNumber(addMid);
        }
    }

    private static int allNine(int n) {
        String str = Integer.toString(n);
        int len = str.length();
        for (char c : str.toCharArray()) {
            if (c != '9') {
                return -1;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(1);
        for (int i = 0; i < len - 1; i++) {
            sb.append(0);
        }
        sb.append(1);

        return Integer.parseInt(sb.toString());
    }

    private static int[] addMid(int[] array) {
        int len = array.length;
        int i = (len - 1) / 2;
        int j = len % 2 == 1 ? i : i + 1;
        int carry = 1;
        while (i >= 0 && carry > 0) {
            array[i] += carry;
            carry = array[i] / 10;
            array[i] %= 10;
            array[j] = array[i];
            i--;
            j++;
        }
        return array;
    }

    private static int arrayToNumber(int[] newArray) {
        int result = 0;
        for (int val : newArray) {
            result = result * 10 + val;
        }
        return result;
    }

    private static int[] mirrorLeft(int[] array) {
        int len = array.length;
        int i = (len - 1) / 2;
        int j = len % 2 == 1 ? i : i + 1;
        int[] copy = new int[len];
        while (i >= 0) {
            copy[i] = array[i];
            copy[j] = copy[i];
            i--;
            j++;
        }
        return copy;
    }

    private static int[] intToArray(int n) {
        String str = Integer.toString(n);
        int len = str.length();
        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = str.charAt(i) - '0';
        }
        return result;
    }

    public static void main(String[] args) {
        int n = 181239;
        System.out.println(nextSmallestPalindrome(n));
    }
}
