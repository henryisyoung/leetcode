package uber;

public class DataStructureOne {
    class Entry {
        int timestamp, val;
        public Entry(int version, int val) {
            this.val = val;
            this.timestamp = version;
        }
    }
    int     id;
    Entry   all;
    Entry[] array;
    public DataStructureOne(int cap) {
        this.array = new Entry[cap];
        this.id = 0;
    }

    public void setValue(int index, int val) {
        array[index] = new Entry(id++, val);
    }

    public int getValue(int index) {
        if (array[index] == null) return -1;
        if(all.timestamp > array[index].timestamp) return all.val;
        return array[index].val;
    }

    public void setAll(int val) {
        all = new Entry(id++, val);
    }
}
