package apple;

public class MaximumNestingDepthTwoValidParenthesesStrings {
    public int[] maxDepthAfterSplit(String seq) {
        int n = seq.length();
        int[] result = new int[n];
        int j = 0;
        int depth = 0;
        for(int i = 0; i < n; i++) {
            char c = seq.charAt(i);
            if(c == '(') {
                depth++;
                result[j++] = depth % 2;
            } else {
                result[j++] = depth % 2;
                depth--;
            }
        }
        return result;
    }
}
