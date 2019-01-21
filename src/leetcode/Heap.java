//package leetcode;
//
//public class Heap {
//    int cap;
//    int[] pq;
//    int index;
//    public Heap(){
//        this.cap = 10;
//        this.pq = new int[cap];
//        this.index = 1;
//    }
//
//    public Heap(int capacity){
//        this.cap = capacity;
//        this.pq = new int[cap];
//    }
//
//    public int remove(){
//        int v = pq[1];
//        pq[1] = pq[index--];
//        sink();
//        return v;
//    }
//
//    private void sink() {
//        int k = 1;
//        while (k < index){
//            if (pq[k] > Math.min()) {
//            }
//        }
//    }
//
//    public void add(int val){
//        index++;
//        pq[index] = val;
//        swim();
//    }
//
//    private void swim() {
//        int k = index;
//        while (k > 1 && pq[k] > pq[k/2]){
//            swap(k,k/2);
//            k = k/2;
//        }
//    }
//
//    private void swap(int son, int dad) {
//        int v = pq[son];
//        pq[son] = pq[dad];
//        pq[dad] = v;
//    }
//
//    public boolean isFull(){
//        return index >= cap;
//    }
//
//    public int peek(){
//        return pq[1];
//    }
//}
