package leetcode.dataStructrue.heap;

public class Heap {
    public int capacity;
    public int[] heap;
    public int size;
    public Heap(int capacity) {
        capacity = capacity;
        heap = new int[capacity];
        size = 0;
    }

    public int peek() {
        return heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(int val) {
        if (size < capacity) {
            heap[size] = val;
            swim(size);
            size++;
        }
    }

    public int pop() {
        int val = heap[0];
        swap(0, size);
        heap[size--] = 0;
        sink(0);
        return val;
    }

    private void swim(int pos) {
        while (pos > 0 && heap[pos] < heap[(pos - 1) / 2]) {
            swap(pos, (pos - 1)/2);
            pos = (pos - 1) / 2;
        }
    }

    private void sink(int pos) {
        while (pos <= size) {
            int smaller = Math.min(heap[pos * 2 + 1], heap[pos * 2 + 2]);
            if (smaller >= heap[pos]) {
                break;
            } else {
                if (smaller == heap[pos * 2 + 1]) {
                    swap(pos, pos * 2 + 1);
                } else {
                    swap(pos, pos * 2 + 2);
                }
            }
        }
    }

    private void swap(int i, int j) {
        int tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }
}
