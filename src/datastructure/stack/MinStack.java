package datastructure.stack;

import java.util.Stack;

public class MinStack {
    Stack<Integer> regular, min;
    public MinStack() {
        this.min = new Stack<>();
        this.regular = new Stack<>();
    }

    public void push(int val) {
        regular.push(val);
        if (min.isEmpty() || min.peek() >= val) {
            min.push(val);
        }
    }

    public void pop() {
        int val = regular.pop();
        if (val == min.peek()) {
            min.pop();
        }
    }

    public int top() {
        return regular.peek();
    }

    public int getMin() {
        return min.peek();
    }
}
