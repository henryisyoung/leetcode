package snap;

import java.util.*;

public class MyHashMap {
    private int numBuckets = 100;
    private ArrayList<LinkedList<Cell>> lists = new ArrayList<>(numBuckets);
    private final double LOAD_FACTOR = 0.7;
    private int numItems = 0;

    public MyHashMap() {
        initializeLists(lists);
    }

    private void initializeLists(ArrayList<LinkedList<Cell>> lists) {
        for (int i = 0; i < numBuckets; i++) {
            lists.add(new LinkedList<>());
        }
    }

    private int createHashCode(int key) {
        return key % numBuckets;
    }

    public void put(int key, int value) {
        // Find the LinkedList that we should put the Cell into.
        int hashCode = createHashCode(key);
        LinkedList<Cell> list = lists.get(hashCode);

        // See if we should overwrite an old Cell.
        for (Cell cell : list) {
            if (cell.key == key) {
                cell.value = value; // overwrites the old value.
                return;
            }
        }

        // Create new Cell and add it to our LinkedList.
        list.add(new Cell(key, value));
        numItems++;

        // Rehash if our load factor is too high.
        double loadFactor = (double) numItems / numBuckets;
        if (loadFactor > LOAD_FACTOR) {
            rehash();
        }
    }

    public int get(int key) {
        // Find the LinkedList that may contain the value.
        int hashCode = createHashCode(key);
        LinkedList<Cell> list = lists.get(hashCode);

        for (Cell cell : list) {
            if (cell.key == key) {
                return cell.value;
            }
        }
        // key doesn't exist.
        return -1; // Fine since "keys/values are in range of [0, 1000000]"
    }

    public void remove(int key) {
        // Find the LinkedList that may contain the value.
        int hashCode = createHashCode(key);
        LinkedList<Cell> list = lists.get(hashCode);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).key == key) {
                list.remove(i);
                if (list.size() == 0) numItems--;
                return;
            }
        }
    }

    private void rehash() {
        ArrayList<LinkedList<Cell>> temp = lists;
        numBuckets *= 2;
        lists = new ArrayList<>(numBuckets);
        initializeLists(lists);
        numItems = 0;
        for (LinkedList<Cell> list : temp) {
            for (Cell cell : list) {
                put(cell.key, cell.value);
            }
        }
    }

    class Cell{
        public int key, value;
        public Cell(int k, int v){
            this.key = k;
            this.value = v;
        }
    }
}


