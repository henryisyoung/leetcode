package uber.VO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayIntersection {
    /*
    2. 高级版的 Find Array Intersection, 没有见过，感觉挺难的。跟面试官要了hint，有了一点思路，但是时间马上快到了，没写完，有小bug.
    Given 2 arrays, compute the intersection of all possible sequences they have.
    example 1:
    Input: nums1 = [4, 9, 5], nums2=[9,4,3,9,5,8,4]
    Output:
    [4]
    [9]
    [5]
    [9,5]
    注意输出的顺序要跟input array里面数字的顺序一样才可以算进输出

    2可以是长的array把val - index存起来 然后iterate另外一个array 比如4 9 5 找下面的4看他们+1有没有9 再加1有没有5
    算到最长的sequence然后把所有子sequence加进return result里面 然后再从下一个数字开始 如果两个array长度分别是m和n的话
    那么time就是o（m+n) space就是o(m)长的那个
     */

    public List<List<Integer>> findArrayIntersection(int[] array1, int[] array2) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<List<Integer>> result = new ArrayList<>();

        int[] longerArray = array1.length > array2.length ? array1 : array2;
        int[] shorterArray = array1.length <= array2.length ? array1 : array2;
        for (int i = 0; i < longerArray.length; i++) {
            map.putIfAbsent(longerArray[i], new ArrayList<>());
            map.get(longerArray[i]).add(i);
        }
        int size = shorterArray.length;
        int i = 0, j = 0;
        while (j < size) {
            while (j < size && map.containsKey(shorterArray[j])) {
                if (j == i) {

                }
            }
        }

        return result;
    }
}
