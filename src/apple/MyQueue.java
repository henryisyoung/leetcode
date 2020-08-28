package apple;

import java.util.Stack;

public class MyQueue {
    Stack<Integer> in, out;
    /** Initialize your data structure here. */
    public MyQueue() {
        this.in = new Stack<>();
        this.out = new Stack<>();
    }

    /** Push element x to the back of queue. */
    public void push(int x) {
        in.push(x);
    }

    /** Removes the element from in front of queue and returns that element. */
    public int pop() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
        return in.pop();
    }

    /** Get the front element. */
    public int peek() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
        return in.peek();
    }

    /** Returns whether the queue is empty. */
    public boolean empty() {
        return in.isEmpty() && out.isEmpty();
    }
}
