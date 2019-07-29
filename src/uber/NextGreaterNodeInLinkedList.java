package uber;

import leetcode.linkedList.ListNode;

import java.util.Arrays;
import java.util.Stack;

public class NextGreaterNodeInLinkedList {
    public static int[] nextLargerNodes(ListNode head) {
        if (head == null) return new int[]{0};
        int count = 0;
        Stack<Integer> reverse = new Stack<>();
        while (head != null) {
            reverse.add(head.val);
            head = head.next;
            count++;
        }
        int[] result = new int[count];
        Stack<Integer> buffer = new Stack<>();
        count--;
        while (!reverse.isEmpty()) {
            if (buffer.isEmpty()) {
                result[count--] = 0;
                buffer.add(reverse.pop());
            } else {
                while (!buffer.isEmpty() && reverse.peek() >= buffer.peek()) {
                    buffer.pop();
                }
                if (buffer.isEmpty()) {
                    result[count--] = 0;
                    buffer.add(reverse.pop());
                } else {
                    result[count--] = buffer.peek();
                    buffer.add(reverse.pop());
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] arr = {1,7,5,1,9,2,5,1};
        ListNode dm = new ListNode(-1);
        ListNode copy = dm;
        for (int i : arr) {
            copy.next = new ListNode(i);
            copy = copy.next;
        }
        int[] result = nextLargerNodes(dm.next);
        System.out.println(Arrays.toString(result));
    }
}
