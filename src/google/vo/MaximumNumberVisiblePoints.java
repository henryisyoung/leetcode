package google.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaximumNumberVisiblePoints {
    public int visiblePoints(List<List<Integer>> points, int angle, List<Integer> location) {
        int originCount = 0;
        List<Double> nodes = new ArrayList<>();
        int ox = location.get(0), oy = location.get(1);
        for (List<Integer> p : points) {
            int px = p.get(0), py = p.get(1);
            if (px == ox && oy == py) {
                originCount++;
            } else {
                nodes.add(Math.atan2(py - oy, px - ox));
            }
        }
        Collections.sort(nodes);
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            nodes.add(nodes.get(i) + 2 * Math.PI);
        }
        int l = 0;
        int result = 0;
        double fixAngle = angle * Math.PI / 180.0;
        for (int r = 0; r < nodes.size(); r++) {
            while (nodes.get(r) - nodes.get(l) > fixAngle) {
                l++;
            }
            result = Math.max(result, r - l + 1);
        }
        return result + originCount;
    }
}
