package twitter;

import java.util.Arrays;

public class SortList {
//    给你一个sorted 全是整数的list，要你返回平方后的list并且依旧保持sorted
//    eg: [1,2,3,4] => [1,4,9,16]
//    数字可以为负
    public static void sortSquares(int arr[]) {
        int n = arr.length;
        // first dived array into part negative and positive
        int k;
        for(k = 0; k < n; k++) {
            if(arr[k] >= 0)
                break;
        }

        // Now do the same process that we learn
        // in merge sort to merge to two sorted array
        // here both two half are sorted and we traverse
        // first half in reverse meaner because
        // first half contain negative element
        int i = k-1; // Initial index of first half
        int j = k; // Initial index of second half
        int ind = 0; // Initial index of temp array

        int[] temp = new int[n];
        while(i >= 0 && j < n) {
            if(arr[i] * arr[i] < arr[j] * arr[j]) {
                temp[ind] = arr[i] * arr[i];
                i--;
            } else{

                temp[ind] = arr[j] * arr[j];
                j++;

            }
            ind++;
        }

        while(i >= 0) {
            temp[ind++] = arr[i] * arr[i];
            i--;
        }
        while(j < n) {
            temp[ind++] = arr[j] * arr[j];
            j++;
        }

        // copy 'temp' array into original array
        for (int x = 0 ; x < n; x++) {
            arr[x] = temp[x];
        }
    }
}
