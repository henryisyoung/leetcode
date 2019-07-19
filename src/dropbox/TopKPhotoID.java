package dropbox;

import java.util.*;
class Photo {
    int id, freq;
    public Photo(int id, int freq) {
        this.id = id;
        this.freq = freq;
    }
}
public class TopKPhotoID {
    // bucket O(n) be care of sparse
    public List<Integer> topKViewPhotoBucket(int[] photoIds, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        int max = 0;
        for (int id : photoIds) {
            map.put(id, map.getOrDefault(id, 0) + 1);
            max = Math.max(max, map.get(id));
        }
        List[] freqs = new List[max + 1];
        List<Integer> result = new ArrayList<>();
        for (int key : map.keySet()) {
            int freq = map.get(key);
            if (freqs[freq] == null) freqs[freq] = new ArrayList();
            freqs[freq].add(key);
        }
        for (int i = max; i >=0 ;i--) {
            if (freqs[max] != null) {
                List<Integer> list = freqs[i];
                if (list.size() <= k) {
                    result.addAll(list);
                    k -= list.size();
                } else {
                    int j = 0;
                    while (j < k) {
                        result.add(list.get(j));
                        j++;
                    }
                    break;
                }
            }
        }
        return result;
    }


    public List<Integer> topKViewPhotoPQ(int[] photoIds, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int id : photoIds) {
            map.put(id, map.getOrDefault(id, 0) + 1);
        }
        PriorityQueue<Photo> pq = new PriorityQueue<>(photoIds.length, (a, b) -> (a.freq - b.freq));
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            pq.add(new Photo(entry.getKey(), entry.getValue()));
        }
        List<Integer> result = new ArrayList<>();
        while (!pq.isEmpty() && k > 0) {
            Photo photo = pq.poll();
            if (photo.freq <= k) {
                k -= photo.freq;
                result.add(photo.id);
            } else {
                k = 0;
                result.add(photo.id);
            }
        }
        return result;
    }
}
