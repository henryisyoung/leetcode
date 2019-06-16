package leetcode.solution;

import java.util.HashMap;

public class Question138_Linked_List {
    class RandomListNode {
        int label;
        RandomListNode next, random;
        RandomListNode(int x) { this.label = x; }
    };

    public RandomListNode copyRandomListNOSPACE(RandomListNode head) {
        // has bug
        if(head == null) return null;
        RandomListNode cur = head;
        while (cur != null){
            RandomListNode tmp = cur.next;
            cur.next = new RandomListNode(cur.label);
            cur.next.next = tmp;
            cur = cur.next.next;
        }
        cur = head;
        while (cur != null && cur.next != null){
            RandomListNode random = cur.random;
            cur.next.random = random.next;
            cur = cur.next.next;
        }
        RandomListNode dm = new RandomListNode(0);
        RandomListNode newHead = dm;
        while (head != null && head.next != null){
            newHead.next = head.next;
            head = head.next.next;
        }
        return dm.next;
    }

    public RandomListNode copyRandomList(RandomListNode head) {
        if(head == null) return null;
        HashMap<RandomListNode, RandomListNode> map = new HashMap<>();
        RandomListNode dm = new RandomListNode(0);
        RandomListNode copy = dm;
        while (head != null){
            RandomListNode cur;
            if(!map.containsKey(head)){
                cur = new RandomListNode(head.label);
                map.put(head, cur);
            }else {
                cur = map.get(head);
            }
            copy.next = cur;
            if(head.random != null){
                if(!map.containsKey(head.random)){
                    cur.random = new RandomListNode(head.random.label);
                    map.put(head.random, cur.random);
                }else {
                    cur.random = map.get(head.random);
                }
            }
            copy = copy.next;
            head = head.next;
        }
        return dm.next;
    }
}
