package google;

import java.util.*;

public class CloestApartment {
    /*
     * Input: 1. 给一条路，路上的不同位置有不同的设施，有多个设施在不同位置的情况, List<Set<String>>
     *        2. 给一个需求设施的set
     * Output： 希望给出一个位置，距离所有设施的距离最近的和
     *
     * Example:
     *   Road: {
     *           [bookstore, school],
     *           [grocery] ,
     *           [],
     *           [],
     *           [bookstore, library],
     *           []
     *           [grocery]
     *        }
     *   Requires: [bookstore, library, grocery]
     *   Output: the best place is 4, to bookstore and lib is 0, and grocery is 2, so in sum is 2.
     *   https://www.1point3acres.com/bbs/thread-505215-1-1.html
     *   https://www.1point3acres.com/bbs/thread-523893-1-1.html
     * */

    public int findBestLocationn(List<Set<String>> road, List<String> requires) {
        Map<String, List<Integer>> roadMap = createMap(road);
        int minSum = Integer.MAX_VALUE, index = 0;
        for (int i = 0; i < road.size(); i++) {
            int sum = 0;
            for (int j = 0; j < requires.size(); j++) {
                sum += getMinLen(roadMap, requires.get(j), i);
            }
            if (sum < minSum) {
                minSum = sum;
                index = i;
            }
        }
        return index;
    }

    private Map<String, List<Integer>> createMap(List<Set<String>> road) {
        Map<String, List<Integer>> roadMap = new HashMap<>();
        for (int i = 0; i < road.size(); i++) {
            for (String facility: road.get(i)) {
                List<Integer> list = roadMap.getOrDefault(facility, new ArrayList<>());
                list.add(i);
                roadMap.put(facility, list);
            }
        }
        return roadMap;
    }

    private int getMinLen(Map<String, List<Integer>> roadMap, String require, int index) {
        List<Integer> list = roadMap.get(require);
        int minLen = Integer.MAX_VALUE;
        for (int pos: list) {
            minLen = Math.min(minLen, Math.abs(pos-index));
        }
        return minLen;
    }
}
