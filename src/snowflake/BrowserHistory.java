package snowflake;

public class BrowserHistory {

    static class Node{
        Node prev, next;
        String url;
        public Node(String url){
            this.url = url;
        }
    }

    Node home;
    Node cur;
    public BrowserHistory(String homepage) {
        this.home = new Node(homepage);
        this.cur = home;
    }

    public void visit(String url) {
        Node node = new Node(url);
        cur.next = node;
        node.prev = cur;
        cur = node;
    }

    public String back(int steps) {
        while (cur.prev != null && steps > 0) {
            steps--;
            cur = cur.prev;
        }
        return cur.url;
    }

    public String forward(int steps) {
        while (cur.next != null && steps > 0) {
            steps--;
            cur = cur.next;
        }
        return cur.url;
    }
}
