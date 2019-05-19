package twitter;

import java.util.*;

public class RangeModule {
    List<int[]> list;
    public RangeModule() {
        this.list = new ArrayList<>();
    }

    public void addRange(int left, int right) {
        int pos = 0;
        List<int[]> newList = new ArrayList<>();
        for (int[] arr : list) {
            if (arr[1] <= left) {
                pos++;
                newList.add(arr);
            } else if (arr[0] >= right) {
                newList.add(arr);
            } else {
                left = Math.min(arr[0], left);
                right = Math.max(arr[1], right);
            }
        }
        newList.add(pos, new int[]{left, right});
        list.clear();
        list.addAll(newList);
    }

    public boolean queryRange(int left, int right) {
        for (int[] arr : list) {
            if (arr[0] <= left && arr[1] >= right) {
                return true;
            }
        }
        return false;
    }

    public void removeRange(int left, int right) {
        int pos = 0;
        int[] insert1 = new int[2], insert2 = new int[2];
        List<int[]> newList = new ArrayList<>();
        for (int[] arr : list) {
            if (arr[1] <= left) {
                pos++;
                newList.add(arr);
            } else if (arr[0] >= right) {
                newList.add(arr);
            } else {
                if (arr[0] < left) {
                    insert1[0] = arr[0];
                    insert1[1] = left;
                }
                if (arr[1] > right) {
                    insert2[0] = right;
                    insert2[1] = arr[1];
                }
            }
        }
        newList.add(pos, insert1);
        newList.add(pos + 1, insert2);
        list.clear();
        list.addAll(newList);
    }

    public static void main(String[] args) {
        RangeModule solver = new RangeModule();
        solver.addRange(6, 8);
        for (int[] arr : solver.list) {
            System.out.print(Arrays.toString(arr) + " ");
        }
        System.out.println();
        solver.removeRange(7, 8);
        for (int[] arr : solver.list) {
            System.out.print(Arrays.toString(arr) + " ");
        }
        System.out.println();
        solver.removeRange(8, 9);
        for (int[] arr : solver.list) {
            System.out.print(Arrays.toString(arr) + " ");
        }
        System.out.println();
        solver.addRange(8, 9);
        for (int[] arr : solver.list) {
            System.out.print(Arrays.toString(arr) + " ");
        }
        System.out.println();

        System.out.println(solver.queryRange(10, 14));
        System.out.println(solver.queryRange(13, 15));
        System.out.println(solver.queryRange(16, 17));
    }
}
