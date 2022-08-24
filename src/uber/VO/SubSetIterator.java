package uber.VO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SubSetIterator implements Iterator<List<Integer>> {
    int k;
    int size;
    int[] indexes;
    List<Integer> source;
    boolean hasNext;
    public SubSetIterator(List<Integer> source, int k) {
        if (k < 1 || k > source.size())
            throw new IllegalArgumentException("k must be between 1 and source.size");
        this.source = source;
        this.k = k;
        this.size = source.size();
        indexes = new int[k];
        for (int i = 0; i < k; i++) {
            indexes[i] = i;
        }
        hasNext = true;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public List<Integer> next() {
        List<Integer> result = new ArrayList<>();
        for (int i : indexes) {
            result.add(source.get(i));
        }
        int i = k - 1, diff = 1;
        while (i >= 0 && indexes[i] == size - diff) {
            i--;
            diff++;
        }
        if (i < 0) {
            hasNext = false;
        } else {
            indexes[i]++;
            while (i < k - 1) {
                indexes[i + 1] = indexes[i] + 1;
                i++;
            }
            hasNext = true;
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> source = Arrays.asList(1,2,3,4);
        SubSetIterator iterator = new SubSetIterator(source, 2);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
