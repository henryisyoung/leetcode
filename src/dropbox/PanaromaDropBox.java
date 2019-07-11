package dropbox;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PanaromaDropBox {
    private class Part {
        int column;
        int row;
        String path;
        Part next;

        public Part(int row, int column) {
            this.row = row;
            this.column = column;
            this.path = "";
            this.next = null;
        }

        public Part(int row, int column, String path) {
            this.row = row;
            this.column = column;
            this.path = path;
            this.next = null;
        }
    }

    Map<Part, Part> keyPrev;
    Part head;
    Part tail;
    int size;
    int capacity;

    int nrows;
    int ncolumns;
    String basePath;
    //Map<Part, String> buffer; //(String is the path of the image)

    public PanaromaDropBox(int capacity, int numOfRows, int numOfColumns, String basePath) {
        this.capacity = capacity;
        this.size = 0;
        this.head = new Part(-1, -1);
        this.tail = head;
        this.keyPrev = new HashMap<>();

        this.nrows = numOfRows;
        this.ncolumns = numOfColumns;
        this.basePath = basePath;
        //this.buffer = new HashMap<>();
    }

    private void moveToBack(Part prev) {
        Part current = prev.next;
        prev.next = current.next;
        keyPrev.put(current.next, prev);
        tail.next = current;
        keyPrev.put(current, tail);
        tail = current;
        current.next = null;
    }

    public void update(int i, int j, Image image) {
        Part current = null;
        for (Part part : keyPrev.keySet()) {
            if (part.row == i && part.column == j) {
                current = part;
                break;
            }
        }

        String imagePath = basePath + hash(image);
        if (current != null) {
            Part prev = keyPrev.get(current);
            if (prev.next != tail) {
                moveToBack(prev);
                tail.path = imagePath;
                return;
            }
            tail.path = imagePath;
            return;
        }

        if (size < capacity) {
            current = new Part(i, j, imagePath);
            tail.next = current;
            keyPrev.put(current, tail);
            tail = current;
            size++;
            return;
        }

        current = new Part(i, j, imagePath);
        tail.next = current;
        keyPrev.put(current, tail);
        tail = current;

        keyPrev.remove(head.next);
        head.next = head.next.next;
        keyPrev.put(head.next, head);

//        String imagePath = basePath + hash(image);
//        Part current = null;
//        for (Part part : buffer.keySet()) {
//            if (part.row == i && part.column == j) {
//                current = part;
//                break;
//            }
//        }
//        if (current == null) {
//            current = new Part(i, j);
//        }
//
//        buffer.put(current, imagePath);
    }
    public Image fetch(int i, int j) {
        Part result = null;
        for (Part part : keyPrev.keySet()/*buffer.keySet()*/) {
            if (part.row == i && part.column == j) {
                result = part;
                break;
            }
        }

        if (result != null) {
            String path = keyPrev.get(result).path;/*buffer.get(result)*/
            read_file(path);
            return null;
        }
        return null;
    }

    public int[] getOldestImage() {
        int[] result = new int[]{head.row, head.column};
        return result;
    }

    /* return unique string given image */
    private String hash(Image image) {
        return "";
    }

    /* 假设如果已经给了api用来read/write imge
    *  这个我有点记不清了 but 这个不重要就就是一些I/O api 他会给出
    */
    private void read_file(String pathOfImage) {

        //return Image;
    }

    private void save_file(Image image){

    }
}