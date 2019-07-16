package dropbox;

import java.awt.*;
import java.util.*;

public class Panarama {
    int numOfRows;
    int numOfColumns;
    String basePath;
    Map<Sector, Node> map;
    Node head, tail;
    public Panarama() {
        this.basePath = "/basePath/";
        this.map = new HashMap<>();
        this.head = new Node(null, "");
        this.tail = new Node(null, "");
    }

    class Sector {
        int col;
        int row;
        public Sector(int row, int col) {
            this.row = row;
            this.col = col;
        }
        public boolean equals(Object o) {
            return o instanceof Sector && ((Sector)o).row == row && ((Sector)o).col == col;
        }
        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + row;
            hashCode = 31 * hashCode + col;
            return hashCode;
        }
    }

    class Node {
        Sector sector;
        String path;
        Node next, prev;
        public Node(Sector sector, String path) {
            this.sector = sector;
            this.path = path;
        }
    }

    public String get(Sector sector) {
        if (!map.containsKey(sector)) return "";
        Node n = map.get(sector);
        n.prev.next = n.next;
        n.next.prev = n.prev;
        moveTail(n);
        return basePath + n.path;
    }

    private void moveTail(Node n) {
        n.next = tail;
        n.prev = tail.prev;
        tail.prev = n.prev;
        n.prev.next = n;
    }

    public void set(Sector sector, Image image) {
//        image.set(path);
        String path = sector.row + "_" + sector.col;
        if (get(sector).equals("")) {
            Node n = new Node(sector, path);
            map.put(sector, n);
            moveTail(n);
        } else {
            Node n = map.get(sector);
            n.path = path;
            map.put(sector, n);
        }
    }

    public String getStalestPath() {
        return head.next.path;
    }
}
