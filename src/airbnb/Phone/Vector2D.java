package airbnb.Phone;

import java.util.NoSuchElementException;

public class Vector2D {
    private int[][] vector;
    private int col = 0;
    private int row = 0;

    public Vector2D(int[][] v) {
        vector = v;
    }

    public int next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return vector[row][col++];
    }

    public boolean hasNext() {
        moveNext();
        return row < vector.length;
    }

    private void moveNext() {
        while (row < vector.length && col == vector[row].length) {
            col = 0;
            row++;
        }
    }
}
