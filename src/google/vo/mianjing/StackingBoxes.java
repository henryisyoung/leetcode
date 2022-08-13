package google.vo.mianjing;

import java.util.Objects;
import java.util.TreeSet;

public class StackingBoxes {
    static class Box{
        int w, h;
        public Box(int w, int h) {
            this.w = w;
            this.h = h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Box)) return false;
            Box box = (Box) o;
            return w == box.w && h == box.h;
        }

        @Override
        public int hashCode() {
            return Objects.hash(w, h);
        }

        @Override
        public String toString() {
            return "Box{" +
                    "w=" + w +
                    ", h=" + h +
                    '}';
        }
    }

    public static int maxBoxes(int[][] boxes) {
        TreeSet<Box> set = new TreeSet<>((a, b) -> {
           if (a.w != b.w) {
               return b.w - a.w;
           }
           return b.h - a.h;
        });
        for (int[] box : boxes) {
            Box b1 = new Box(box[0], box[1]);
            Box b2 = new Box(box[1], box[0]);
            set.add(b1);
            set.add(b2);
        }

        Box prev = set.pollFirst();
        Box samePrev = new Box(prev.h, prev.w);
        set.remove(samePrev);
        int count = 0;
        while (set.size() > 0) {
            Box cur = set.pollFirst();;
            if (cur.w < prev.w) {
                count++;
                prev = cur;
                set.remove(new Box(cur.h, cur.w));
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[][] boxes = {{2,2},{3,2},{3,4},{4,3}};
        System.out.println(maxBoxes(boxes));
    }


}
