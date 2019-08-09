//package snap;
//
//import java.util.Stack;
//
//public class MaxStack  {
//    class T implements Comparable<T> {
//        int val;
//        public T(int val) {
//            this.val = val;
//        }
//        @Override
//        public int compareTo(T that) {
//            return that.val - this.val;
//        }
//    }
//
//    private Stack<T> stack, max;
//
//    public MaxStack() {
//        this.stack = new Stack<>();
//        this.max = new Stack<>();
//    }
//
//
//    public void push(T x) {
//        stack.push(x);
//        if (x.compareTo(max.peek()) > 0) {
//            max.push(x);
//        }
//    }
//
//    public void pop() {
//        T val = stack.pop();
//        if (max.peek().compareTo(val) == 0) {
//            max.pop();
//        }
//    }
//
//    public T top() {
//        return stack.peek();
//    }
//
//    public T getMax() {
//        return max.peek();
//    }
//}
