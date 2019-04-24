package airbnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * learn
 * 1. iterator has three method hasNext next remvoe
 * 2. 如果一道题是要实现这三个methods 尽量去Implements iterator 利用原来的method 进行override就行
 */
public class Flatten2DVectorNoIters implements Iterator<Integer> {
    int row, col;
    List<List<Integer>> lists;
    public Flatten2DVectorNoIters(List<List<Integer>> vec2d) {
        this.lists = new ArrayList<>();
        for (List<Integer> list : vec2d) {
            if (list == null || list.size() == 0) {
                continue;
            }
            lists.add(list);
        }
        this.row = 0;
        this.col = 0;
    }

    @Override
    public boolean hasNext() {
        if (row < lists.size() - 1) {
            return true;
        } else if (row >= lists.size()) {
            return false;
        } else {
            return col < lists.get(row).size();
        }
    }

    @Override
    public Integer next() {
        int val = lists.get(row).get(col);
        col++;
        if (col == lists.get(row).size()) {
            row++;
            col = 0;
        }
        return val;
    }

    @Override
    public void remove() {
        if (col == 0) {
            int oldR = row - 1;
            int oldC = lists.get(oldR).size() - 1;
            lists.get(oldR).remove(oldC);
            if (lists.get(oldR).size() == 0) {
                lists.remove(oldR);
                row--;
            }
        } else {
            lists.get(row).remove(col - 1);
            col--;
            if (lists.get(row).size() == 0) {
                lists.remove(row);
                row--;
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> l1 = new ArrayList<>(Arrays.asList(-1,30,34,40,5));
        List<Integer> l2 = new ArrayList<>(Arrays.asList(-41,3,3740,4,5));
        List<Integer> l3 = new ArrayList<>(Arrays.asList(-1,36,347,8440,5));
        List<Integer> l4 = new ArrayList<>(Arrays.asList(-610,365,384,84,5));
        List<Integer> l5 = new ArrayList<>(Arrays.asList(-18,3,34,480,845));
        List<Integer> l8 = new ArrayList<>(Arrays.asList(-44444445));
//        Iterator test = l5.iterator();
//        test.next();
//        test.remove();
//        System.out.println(l5.toString());
        List<Integer> l6 = null;
        List<Integer> l7 = new ArrayList<>();
        List<List<Integer>> vec2d = new ArrayList<>();
        vec2d.add(l1);
        vec2d.add(l2);
        vec2d.add(l3);
        vec2d.add(l8);
        vec2d.add(l4);
        vec2d.add(l5);
        vec2d.add(l6);
        vec2d.add(l7);

        Flatten2DVectorNoIters iter = new Flatten2DVectorNoIters(vec2d);
        List<List<Integer>> lists = iter.lists;
        for (List<Integer> list : lists) {
            System.out.println(list.toString());
        }
        while (iter.hasNext()) {
            int next = iter.next();
            System.out.println("cur : " + next );
            System.out.println("row " + iter.row + " col " + iter.col);
            if (next % 5 == 0) {
                iter.remove();
                System.out.println("after remove: row " + iter.row + " col " + iter.col);
            }
        }
        System.out.println(vec2d.toString());
    }
}
