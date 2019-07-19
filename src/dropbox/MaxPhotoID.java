package dropbox;

import jdk.nashorn.internal.ir.IfNode;

import java.util.*;

public class MaxPhotoID {
    int k;
    Map<Integer, Photo> freqMap;
    PriorityQueue<Photo> pq;

    public MaxPhotoID(int k) {
        this.k = k;
        this.freqMap = new HashMap<>();
        this.pq = new PriorityQueue<>(k, (a, b) -> (a.freq - b.freq));
    }

    public void view(int id) {
        if (!freqMap.containsKey(id)) {
            freqMap.put(id, new Photo(id, 0));
        }
        Photo photo = freqMap.get(id);
        photo.freq++;
        if (pq.size() < k || pq.peek().freq < photo.freq) {
            pq.remove(photo);
            pq.offer(photo);
            if (pq.size() > k) pq.poll();
        }
    }

    public List<Integer> getTopKViewPhoto() {
        Photo[] topK = pq.toArray(new Photo[pq.size()]);

        List<Integer> result = new ArrayList<>();
        for(Photo photoView: topK) {
            result.add(photoView.id);
        }
        return result;
    }

    public static void main (String[] args) {
        MaxPhotoID solver = new MaxPhotoID(4);
        solver.view(1);
        solver.view(2);
        solver.view(1);
        System.out.println(solver.getTopKViewPhoto());
        solver.view(3);

        System.out.println(solver.getTopKViewPhoto());
        solver.view(2);
        solver.view(12);
        solver.view(31);
        solver.view(101);
        solver.view(11);
        solver.view(3);
        System.out.println(solver.getTopKViewPhoto());

        solver.view(31);
        solver.view(101);
        solver.view(31);
        solver.view(101);
        System.out.println(solver.getTopKViewPhoto());
    }
}
