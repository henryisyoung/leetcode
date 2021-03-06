package google;

import java.util.*;

public class ArraysDifference {
    // find all element in A not in B https://www.1point3acres.com/bbs/thread-524322-1-1.html
    // https://www.1point3acres.com/bbs/thread-521575-1-1.html
    // 如果A{pA} < B{pB} 那A{pA}肯定是 element in A that not in B --> pA++
    // 如果A{pA} > B{pB} 那B{pB}肯定是 element in B that not in A --> pB++
    // 如果A{pA} == B{pB} 那A{pA}和B{pB}都不符合要求 --> pA++, pB++
    public List<Integer> findDiffSorted(Integer[] A, Integer[] B) {
        List<Integer> list = new ArrayList<>();
        if (B == null || B.length == 0) {
            return Arrays.asList(A);
        }
        if (A == null || A.length == 0) {
            return list;
        }
        int m = A.length, n = B.length;
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (A[i] == B[j]) {
                i++;
                j++;
            } else if (A[i] < B[j]) {
                list.add(A[i]);
                i++;
            } else {
                j++;
            }
        }
        while (i < m) {
            list.add(A[i++]);
        }
        return list;
    }

    public List<Integer> findDiffUnSorted(List<Integer> A, List<Integer> B) {
        List<Integer> list = new ArrayList<>();
        if (B == null || B.size() == 0) {
            return A;
        }
        if (A == null || A.size() == 0) {
            return list;
        }
        Set<Integer> set = new HashSet<>();
        set.addAll(B);
        for (int i : A) {
            if (!set.contains(i)) {
                list.add(i);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Integer[] A= {1,2,3, 4, 6,8}, B = {1, 2, 4, 8};
        ArraysDifference solver = new ArraysDifference();
        List<Integer> list = solver.findDiffSorted(A, B);
        System.out.println(list.toString());
    }
}
