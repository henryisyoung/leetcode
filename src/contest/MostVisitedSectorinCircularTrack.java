package contest;

import java.util.ArrayList;
import java.util.List;

public class MostVisitedSectorinCircularTrack {
    public static List<Integer> mostVisited(int n, int[] rounds) {
        List<Integer> result = new ArrayList<>();
        for (int i = rounds[0]; i <= rounds[rounds.length - 1]; i++) {
            result.add(i);
        }
        if (result.size() > 0) return result;
        for (int i = 1; i <= rounds[rounds.length - 1]; i++) result.add(i);

        for (int i = rounds[0]; i <= n; i++) result.add(i);

        return result;
    }

    public static void main(String[] args) {
        int n = 7;
        int[] rounds = {5,7,2};
        System.out.println(mostVisited(n, rounds));
    }
}
