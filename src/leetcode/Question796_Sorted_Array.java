package leetcode;

public class Question796_Sorted_Array {
    public boolean rotateString(String A, String B) {
        return A.length() == B.length() && (A + A).contains(B);
    }
}
