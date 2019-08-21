package dropbox;

import java.util.*;

public class TopKPhotoIDStream {
    class Photo {
        int id, freq;
        public Photo(int id, int freq) {
            this.id = id;
            this.freq = freq;
        }
    }
    PriorityQueue<Photo> pq;
    Map<Integer, Photo> map;
    int k;
    public TopKPhotoIDStream(int k) {
        this.pq = new PriorityQueue<>((a, b) -> (a.freq - b.freq));
        this.map = new HashMap<>();
        this.k = k;
    }

    public void view(int id) {
        map.putIfAbsent(id, new Photo(id, 0));
        map.get(id).freq++;
        int freq = map.get(id).freq;
        Photo photo = map.get(id);
        if (pq.size() < k || pq.peek().freq < freq) {
            pq.remove(photo);
            pq.add(photo);
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

}
