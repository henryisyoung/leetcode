package airbnb;

public class QueueWithLimitedSizefOfArrays3 {
    class Node {
        Node prev, next;

        int[] list;
        public Node(int size) {
            this.list = new int[size];
        }
    }

    Node headNode, tailNode;
    int headPos, tailPos;
    int count, size;
    public QueueWithLimitedSizefOfArrays3(int fixedSize) {
        this.headNode = new Node(fixedSize);
        this.tailNode = headNode;
        this.headPos = 0;
        this.tailPos = 0;
        this.count = 0;
        this.size = fixedSize;
    }

    public void enqueue(int num) {
        count++;
        tailNode.list[tailPos++] = num;
        if (tailPos == size) {
            Node node = new Node(size);
            tailNode.next = node;
            node.prev = tailNode;
            tailPos = 0;
            tailNode = node;
        }
    }

    public Integer pop() {
        if (count == 0) {
            throw new RuntimeException("no val");
        }
        count--;
        if (headPos == size) {
            headPos = 0;
            headNode = headNode.next;

        }
        return headNode.list[headPos++];
    }

    public Integer peek() {
        if (count == 0) {
            throw new RuntimeException("no val");
        }
        if (headPos == size) {
            headPos = 0;
            headNode = headNode.next;
        }
        return headNode.list[headPos];
    }

    public int size() {
        return count;
    }

    public static void main(String[] args) {
        QueueWithLimitedSizefOfArrays3 m = new QueueWithLimitedSizefOfArrays3(3);
        m.enqueue(1);
        m.enqueue(2);
        m.enqueue(3);
        System.out.println(m.peek()); // 1
        System.out.println(m.pop());  // 1
        System.out.println(m.peek()); // 2
        System.out.println(m.pop());  // 2
        m.enqueue(4);
        m.enqueue(5);
        m.enqueue(6);
        m.enqueue(7);
        m.enqueue(8);
        m.enqueue(9);
        System.out.println(m.peek()); // 3
        System.out.println(m.pop());  // 3
        System.out.println(m.peek()); // 4
        System.out.println(m.pop());  // 4
        System.out.println(m.peek()); // 5
        System.out.println(m.pop());  // 5
        System.out.println(m.peek()); // 6
        System.out.println(m.pop());//  6
        System.out.println(m.peek());// 7
        System.out.println(m.pop());// 7
        System.out.println(m.peek());// 8
        System.out.println(m.pop()); // 8
        System.out.println(m.peek()); // 9
        System.out.println(m.pop()); // 9
//        System.out.println(m.peek());
//        System.out.println(m.pop());
    }
}
