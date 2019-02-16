package leetcode.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Flatten2DVector implements Iterator<Integer> {
    List<Iterator<Integer>> iters = new ArrayList<>();
    int index = 0;
    public Flatten2DVector(List<List<Integer>> vec2d) {
        for (List<Integer> vec : vec2d) {
            if (vec == null || vec.size() == 0) {
                continue;
            }
            iters.add(vec.iterator());
        }
    }

    @Override
    public Integer next() {
        while (index < iters.size()) {
            if (iters.get(index).hasNext()) {
                return iters.get(index).next();
            }
            index++;
        }
        return null;
    }

    @Override
    public void remove() {

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
}
