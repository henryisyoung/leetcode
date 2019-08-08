//package snap;
//
//import java.util.*;
//
//public class HierarchyIndentBraces {
//    public static void printBraces(String str) {
//        int count = 0;
//        Set<Character> set = new HashSet<>();
//        set.addAll(Arrays.asList(' ', '(', ')'));
//        dfsPrint(count, str, set, 0, "");
//    }
//
//    private static void dfsPrint(int count, String str, Set<Character> set, int pos, String prev) {
//        if (pos == str.length()) return;
//        char c = str.charAt(pos);
//        if(c == '(') {
//            for (int i = 0; i < count; i++) System.out.print(" ");
//            count++;
//        } else if(c == ')') {
//            count--;
//
//        }
//
//    }
//
//    public static void main(String[] args) {
//        printBraces("(hi aloha (hello))");
//    }
//}
