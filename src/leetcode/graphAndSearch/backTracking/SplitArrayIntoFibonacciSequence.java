package leetcode.graphAndSearch.backTracking;

import java.util.*;

public class SplitArrayIntoFibonacciSequence {
    List<Integer> result;

    public void traverse(List<Integer> current, String S, int prevIndex, int index) {
        // 3. The sequence should has at least 3 elements
        if( index == S.length() && current.size() > 2)
            result = new ArrayList<Integer>(current);
        else{
            for(int i = index; i < S.length(); i++) {
                // 1. Remove elements with leading zero
                if( S.charAt(prevIndex) == '0' && i + 1 - prevIndex > 1 )
                    break;

                long longValue = Long.parseLong(S.substring(prevIndex, i + 1));
                // 2. The element in the sequence should be at most Integer.MAX_VALUE
                if( longValue > Integer.MAX_VALUE )
                    break;
                int size = current.size();
                if( current.size() < 2 ) {
                    current.add((int) longValue);
                    traverse(current, S, i + 1, i + 1);
                    // 5. If we find a valid sequence, stop backtracking
                    if( result != null )
                        break;
                    current.remove(current.size() - 1);
                }
                else{
                    long sum = (long)current.get(size - 1) + (long)current.get(size - 2);
                    // 4. If current number is larger than the sum of previous two elements, stop backtracking
                    if( longValue > sum )
                        break;
                    else if( longValue < sum )
                        continue;
                    else {
                        current.add((int) longValue);
                        traverse(current, S, i + 1, i + 1);
                        if( result != null )
                            break;
                        current.remove(current.size() - 1);
                    }
                }
            }
        }
    }

    public List<Integer> splitIntoFibonacci(String S) {
        List<Integer> current = new ArrayList<Integer>();
        traverse(current, S, 0, 0);
        return result != null ? result : new ArrayList<Integer>();
    }
}
