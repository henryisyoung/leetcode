package recovery;

import java.util.Stack;

public class MinStack {
    Stack<Integer> regular, min;
    public MinStack() {
        this.regular = new Stack<>();
        this.min = new Stack<>();
    }

    public void push(int val) {
        regular.add(val);
        if (min.isEmpty() || min.peek() >= val) min.add(val);
    }

    public void pop() {
        int val = regular.pop();
        if (val == min.peek()) min.pop();
    }

    public int top() {
        return regular.peek();
    }

    public int getMin() {
        return min.peek();
    }
}
