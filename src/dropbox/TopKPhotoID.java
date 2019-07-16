package dropbox;

import java.util.*;

public class TopKPhotoID {

    public List<Integer> topKViewPhoto(int[] photoIds, int k) {
        List<Integer> result = new ArrayList<>();
        if(photoIds==null || photoIds.length<1) return result;

        Map<Integer, Integer> freqMap = new HashMap<>();
        int maxFreq = 0;
        for(int id: photoIds) {
            int freq = freqMap.getOrDefault(id, 0)+1;
            maxFreq = Math.max(maxFreq, freq);
            freqMap.put(id, freq);
        }

        List[] freqBuckets = new List[maxFreq + 1];
        for(Map.Entry<Integer, Integer> freqEntry: freqMap.entrySet()) {
            if(freqBuckets[freqEntry.getValue()]==null) {
                freqBuckets[freqEntry.getValue()] = new ArrayList<>();
            }
            freqBuckets[freqEntry.getValue()].add(freqEntry.getKey());
        }

        for(int i=freqBuckets.length-1; i>=0; i--) {
            if(freqBuckets[i]!=null) {
                List<Integer> ids = freqBuckets[i];
                if(k>=freqBuckets[i].size()) {
                    result.addAll(ids);
                    k -= freqBuckets[i].size();
                } else {
                    for(int j=0; j<k; j++) {
                        result.add(ids.get(j));
                    }
                    break;
                }
            }
        }
        return result;
    }

    public List<Integer> topKViewPhotoPQ(int[] photoIds, int k) {
        List<Integer> result = new ArrayList<>();
        if(photoIds==null || photoIds.length<1) return result;

        Map<Integer, Integer> freqMap = new HashMap<>();
        for(int id: photoIds) {
            int freq = freqMap.getOrDefault(id, 0)+1;
            freqMap.put(id, freq);
        }

        PriorityQueue<View> topKView = new PriorityQueue<>((a,b)->a.freq - b.freq);
        for(Map.Entry<Integer, Integer> freqEntry: freqMap.entrySet()) {
            View view = new View(freqEntry.getKey(), freqEntry.getValue());
            if(topKView.size() < k) {
                topKView.add(view);
            } else {
                if(freqEntry.getValue() > topKView.peek().freq) {
                    topKView.poll();
                    topKView.offer(view);
                }
            }
        }

        while(!topKView.isEmpty()) {
            result.add(topKView.poll().id);
        }

        return result;

    }

    class View {
        int id;
        int freq;
        public View(int id, int freq) {
            this.id = id; this.freq = freq;
        }
    }
}
