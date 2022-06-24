package apple;

import java.util.BitSet;

public class PaintWall {
    BitSet bitSet;
    int n;
    public PaintWall(int n) {
        this.bitSet = new BitSet(n);
        this.n = n;
    }

    public void paint(int i) {
        bitSet.set(i);
    }

    public boolean isDone() {
        return bitSet.nextClearBit(0) < n;
    }

    public static void main(String[] args) {

    }
}
