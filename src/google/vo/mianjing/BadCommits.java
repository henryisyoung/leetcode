package google.vo.mianjing;

import java.util.ArrayList;
import java.util.List;

public class BadCommits {
    // O(k log(n/k))
    public List<Integer> findBadCommits(int n) {
        List<Integer> result = new ArrayList<>();
        findAllBads(result, 0, n);
        return result;
    }

    private void findAllBads(List<Integer> result, int start, int end) {
        if (!isWorse(start, end)) {
            return;
        }
        if (start + 1 == end) {
            result.add(end);
            return;
        }
        int mid = start + (end - start) / 2;
        findAllBads(result, start, mid);
        findAllBads(result, mid, end);
    }

    private boolean isWorse(int id1, int id2) {
        int[] a = {1,2};
        return true;
    }
}
