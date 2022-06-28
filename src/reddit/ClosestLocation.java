package reddit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClosestLocation {
    public int[] closestLocation(List<int[]> homes) {
        List<Integer> rows = new ArrayList<>(), cols = new ArrayList<>();
        if (homes == null || homes.size() == 0) return new int[0];
        for (int[] home : homes) {
            rows.add(home[0]);
            cols.add(home[0]);
        }
        Collections.sort(rows);
        Collections.sort(cols);
        int row = rows.get(rows.size() / 2);
        int col = rows.get(cols.size() / 2);

        return new int[]{row, col};
    }
}
