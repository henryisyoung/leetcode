package facebook;

import leetcode.linkedList.ListNode;

import java.util.Stack;

public class NextGreaterNodeInLinkedList {
    public int[] nextLargerNodes(ListNode head) {
        Stack<Node> stack = new Stack<>();
        int n = findLen(head);
        int[] result = new int[n];
        int i = 0;
        while (head != null) {
            while (!stack.isEmpty() && head.val > stack.peek().val) {
                Node node = stack.pop();
                result[node.pos] = head.val;
            }
            stack.push(new Node(head.val, i++));
            head = head.next;
        }
        return result;
    }

    private int findLen(ListNode head) {
        int n = 0;
        while (head != null) {
            head = head.next;
            n++;
        }
        return n;
    }

    class Node{
        int pos, val;
        public Node(int val, int pos) {
            this.pos = pos;
            this.val = val;
        }
    }


}
