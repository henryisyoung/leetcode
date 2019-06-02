package leetcode.twoPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ZigzagIterator {
    Iterator<Integer> i1, i2;
    int index = 0;

    public ZigzagIterator(List<Integer> v1, List<Integer> v2) {
        this.i1 = v1.iterator();
        this.i2 = v2.iterator();
    }

    public int next() {
        index++;
        if(index % 2 == 1){
            if(i1.hasNext()){
                return i1.next();
            }else{
                return i2.next();
            }
        }else{
            if(i2.hasNext()){
                return i2.next();
            }else{
                return i1.next();
            }
        }
    }

    public boolean hasNext() {
        return i1.hasNext() || i2.hasNext();
    }

    public static void main(String[] args) {
        List<Integer> v1 = Arrays.asList(1,2), v2 = Arrays.asList(3,4,5,6);
        ZigzagIterator iterator = new ZigzagIterator(v1, v2);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
