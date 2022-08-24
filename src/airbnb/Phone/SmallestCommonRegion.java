package airbnb.Phone;

import java.util.*;

public class SmallestCommonRegion {
    public String findSmallestRegion(List<List<String>> regions, String region1, String region2) {
        Map<String, String> map = new HashMap<>();

        for (List<String> list : regions) {
            String place = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                map.put(list.get(i), place);
            }
        }
        Set<String> parents = new HashSet<>();
        while (region1 != null) {
            parents.add(region1);
            region1 = map.get(region1);
        }

        while (region2 != null) {
            if (parents.contains(region2)) {
                return region2;
            }
            region2 = map.get(region2);
        }

        return null;
    }
}
