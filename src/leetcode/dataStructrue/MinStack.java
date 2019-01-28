package leetcode.dataStructrue;

import java.util.Stack;

public class MinStack {
    /** initialize your data structure here. */
    Stack<Integer> regularStack, minStack;
    public MinStack() {
        regularStack = new Stack<>();
        minStack = new Stack<>();
    }

    public void push(int x) {
        regularStack.push(x);
        if(minStack.isEmpty() || x <= minStack.peek()) {
            minStack.push(x);
        }
    }

    public void pop() {
        int val = regularStack.pop();
        if (minStack.peek() == val) {
            minStack.pop();
        }
    }

    public int top() {
        return regularStack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack obj = new MinStack();
 * obj.push(x);
 * obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.getMin();
 */
