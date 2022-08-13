package airtable.VO;

import java.util.ArrayList;
import java.util.List;

public class IntersectionofArrays {
    /*
    上来口述李口散死就的解法，然后扩展到n array，follow up如果array过大怎么办，如果一个array大另一个小怎么办

    Example inverted index:
    Cat =>
    [1, 5, 6, 9, 10, 12, 16, 19, 22...]
    Dog =>
    [3, 5, 7, 9, 16, 17, 18, 19, 21...]
    Elephant =>
    [2, 6, 7, 9, 16, 19, 21, 25, 27...]
    (Postings lists in this inverted index are always sorted)
    Given an input of search terms (eg: [Cat, Dog]), and an inverted index (like above),
    return all document ids that contain all of search terms (eg: Cat, Dog => [5, 9, 16, 19...])
     */

    // O(m + n)
    public static List<Integer> findDuplicateItems(int[] list1, int[] list2) {
        int i = 0, j = 0;
        int m = list1.length, n = list2.length;
        List<Integer> result = new ArrayList<>();
        while (i < m && j < n) {
            if (list1[i] == list2[j]) {
                result.add(list1[i]);
                i++;
                j++;
            } else if (list1[i] > list2[j]) {
                j++;
            } else {
                i++;
            }
        }
        return result;
    }

    // o(nlogm)
    public static List<Integer> findDuplicateItemsOpt(int[] list1, int[] list2) {
        int m = list1.length, n = list2.length;
        // m >>> n
        List<Integer> result = new ArrayList<>();

        for (int  val : list2) {
            int pos = findPos(val, list1);
            if (pos != -1) {
                result.add(list1[pos]);
            }
        }
        return result;
    }

    private static int findPos(int val, int[] list1) {
        int l = 0, r = list1.length - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (list1[mid] == val) {
                return mid;
            } else if (list1[mid] > val) {
                r = mid;
            } else {
                l = mid;
            }
        }
        if (list1[r] == val) {
            return r;
        }
        if (list1[l] == val) {
            return l;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a = new int[]{1, 5, 6, 9, 10, 12, 16, 19, 22};
        int[] b = new int[]{3, 5, 7, 9, 16, 17, 18, 19, 21};

        System.out.println(findDuplicateItems(a, b));
        System.out.println(findDuplicateItemsOpt(a, b));
    }
}
