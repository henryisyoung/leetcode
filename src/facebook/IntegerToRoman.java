package facebook;

import java.util.TreeMap;

public class IntegerToRoman {
    public static String intToRoman(int num) {
        TreeMap<Integer, String> values = new TreeMap<>();
        values.put(1, "I");
        values.put(4, "IV");
        values.put(5, "V");
        values.put(9, "IX");
        values.put(10, "X");
        values.put(40, "XL");
        values.put(50, "L");
        values.put(90, "XC");
        values.put(100, "C");
        values.put(400, "CD");
        values.put(500, "D");
        values.put(900, "CM");
        values.put(1000, "M");
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            Integer key = values.floorKey(num);
            if (key != null) {
                sb.append(values.get(key));
                num -= key;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int num = 58;
        System.out.println(intToRoman(1994));
    }
}
