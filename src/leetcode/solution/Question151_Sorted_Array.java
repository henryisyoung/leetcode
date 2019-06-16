package leetcode.solution;

public class Question151_Sorted_Array {
    public String reverseWords(String s) {
        if(s == null || s.length() < 1) return "";
        String[] array = s.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = array.length - 1; i >= 0; --i) {
            if(!array[i].equals(" ")){
                sb.append(array[i] + " ");
            }
        }

        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }
}
