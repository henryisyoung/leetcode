package leetcode;
import java.util.*;
public class MyStack {
	
	Queue<Integer> q1 = new LinkedList<Integer>();
	Queue<Integer> q2 = new LinkedList<Integer>();
	
	public void push(int x) {
        q1.offer(x);
    }

    // Removes the element on top of the stack.
    public void pop() {
        while(q1.size()>1){
        	q2.offer(q1.poll());
        }
        q1.poll();
        Queue<Integer> tmp = q1;
        q1 = q2;
        q2 = tmp;
    }

    // Get the top element.
    public int top() {
        while (q1.size() > 1) {  
            q2.offer(q1.poll());  
        }  
        int top = q1.peek();  
        q2.offer(q1.poll());  
          
        Queue<Integer> tmp = q1;  
        q1 = q2;  
        q2 = tmp;  
          
        return top;  
    }

    // Return whether the stack is empty.
    public boolean empty() {
        return q1.size() == 0;
    }
}
