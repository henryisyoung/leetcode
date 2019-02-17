package airbnb;

import java.util.*;

/**
 * learn
 * 1. iterator has three method hasNext next remvoe
 * 2. 如果一道题是要实现这三个methods 尽量去Implements iterator 利用原来的method 进行override就行
 */
public class Flatten2DVector implements Iterator<Integer> {

    List<Iterator> iters;
    int index, last;
    public Flatten2DVector(List<List<Integer>> vec2d) {
        this.iters = new ArrayList<>();
        this.index = 0;
        this.last = -1;
        for (List<Integer> vec : vec2d) {
            if (vec == null || vec.size() == 0) {
                continue;
            }
            iters.add(vec.iterator());
        }
    }

    @Override
    public boolean hasNext() {
        if (index == iters.size()) {
            return false;
        } else if (index < iters.size() - 1) {
            return true;
        } else {
            return iters.get(index).hasNext();
        }
    }

    @Override
    public Integer next() {
        while (index < iters.size()) {
            if (iters.get(index).hasNext()) {
                return (Integer) iters.get(index).next();
            }
            index++;
        }
        return null;
    }

    @Override
    public void remove() {
        iters.get(index).remove();
    }

    public static void main(String[] args) {
        List<Integer> l1 = new ArrayList<>(Arrays.asList(-1,30,34,40,5));
        List<Integer> l2 = new ArrayList<>(Arrays.asList(-41,3,3740,4,5));
        List<Integer> l3 = new ArrayList<>(Arrays.asList(-1,36,347,8440,5));
        List<Integer> l4 = new ArrayList<>(Arrays.asList(-610,365,384,84,5));
        List<Integer> l5 = new ArrayList<>(Arrays.asList(-18,3,34,480,845));
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
        vec2d.add(l4);
        vec2d.add(l5);
        vec2d.add(l6);
        vec2d.add(l7);
        Flatten2DVector iter = new Flatten2DVector(vec2d);
        while (iter.hasNext()) {
            int next = iter.next();
            System.out.println("cur : " + next );
            if (next % 5 == 0) {
                iter.remove();
            }
        }
        System.out.println(vec2d.toString());
    }
}
