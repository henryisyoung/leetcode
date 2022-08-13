package roblox.onsite;

import java.util.ArrayList;
import java.util.List;

public class BrowserHistoryMultiTap {
    class Node {
        Node prev, next;
        String url;
        public Node(String string) {
            this.url = string;
        }
    }

    List<Tap> taps;
    String homepage;
    Tap curTap;
    public BrowserHistoryMultiTap(String homepage) {
        this.taps = new ArrayList<>();
        this.homepage = homepage;
        openTap();
    }

    public Tap openTap() {
        Tap tap = new Tap(homepage);
        taps.add(tap);
        curTap = tap;
        return tap;
    }

    public void closeTap(int index) {
        if (index >= taps.size()) {
            throw new IllegalStateException("no valid index");
        }
        Tap remove = taps.get(index);

        if (remove == curTap) {
            curTap = index == taps.size() - 1 ? taps.get(index - 1) : taps.get(index + 1);
        }
        taps.remove(index);
    }

    public String forward(int step) {
        return curTap.forward(step);
    }

    class Tap {
        Node home;
        Node cur;
        public Tap(String homepage) {
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


}
