package roblox.onsite;

public class BrowserHistory {

    class Node {
        Node prev, next;
        String url;
        public Node(String string) {
            this.url = string;
        }
    }

    Node home;
    Node cur;
    public BrowserHistory(String homepage) {
        this.home = new Node(homepage);
        this.cur = home;
    }

    public void visit(String url) {
        Node newNode = new Node(url);
        newNode.prev = cur;
        cur.next = newNode;
        cur = cur.next;
    }

    public String back(int steps) {
        while (cur.prev != null && steps-- > 0) {
            cur = cur.prev;
        }
        return cur.url;
    }

    public String forward(int steps) {
        while (cur.next != null && steps-- > 0) {
            cur = cur.next;
        }
        return cur.url;
    }
}
