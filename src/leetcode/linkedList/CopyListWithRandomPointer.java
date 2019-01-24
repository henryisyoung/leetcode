package leetcode.linkedList;

import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer {
    public RandomListNode copyRandomList(RandomListNode head) {
        if(head == null) {
            return null;
        }
        RandomListNode dummy = new RandomListNode(0);
        RandomListNode copy = dummy;
        Map<RandomListNode, RandomListNode> map = new HashMap<>();
        while (head != null) {
            RandomListNode cur;
            if(! map.containsKey(head)) {
                cur = new RandomListNode(head.label);
                map.put(head, cur);
            } else {
                cur = map.get(head);
            }
            copy.next = cur;
            if(head.random != null) {
                RandomListNode node = head.random;
                if(! map.containsKey(node)) {
                    cur.random = new RandomListNode(node.label);
                    map.put(node, cur.random);
                } else {
                    cur.random = map.get(node);
                }
            }
            copy = copy.next;
            head = head.next;
        }
        return dummy.next;
    }

    public RandomListNode copyRandomListNoMap(RandomListNode head) {
        if(head == null) {
            return null;
        }
        RandomListNode dummy = new RandomListNode(0);
        RandomListNode copy = dummy;
        copyLiist(head);
        addRandomPointer(head);
        return newCopyList(head);
    }

    private void addRandomPointer(RandomListNode head) {
        RandomListNode cur = head;
        while (cur != null && cur.next != null) {
            if(cur.random != null) {
                cur.next.random = cur.random.next;
                cur = cur.next.next;
            }
        }
    }

    private RandomListNode newCopyList(RandomListNode head) {
        RandomListNode dummy = new RandomListNode(0);
        RandomListNode copy = dummy;
        while (head != null && head.next != null) {
            copy.next = head.next;
            head.next = head.next.next;
            copy = copy.next;
        }
        return dummy.next;
    }

    private void copyLiist(RandomListNode head) {
        RandomListNode cur = head;
        while (cur != null) {
            RandomListNode tmp = new RandomListNode(cur.label);
            tmp.next = cur.next;
            cur.next = tmp;
            cur = cur.next.next;
        }
    }
}
