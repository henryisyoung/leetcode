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
        List<Integer> result = new ArrayList<>();
        if(photoIds==null || photoIds.length<1) return result;

        Map<Integer, Integer> freqMap = new HashMap<>();
        for(int id: photoIds) {
            int freq = freqMap.getOrDefault(id, 0)+1;
            freqMap.put(id, freq);
        }

        PriorityQueue<Photo> topKView = new PriorityQueue<>((a,b)->a.freq - b.freq);
        for(Map.Entry<Integer, Integer> freqEntry: freqMap.entrySet()) {
            Photo view = new Photo(freqEntry.getKey(), freqEntry.getValue());
            if(topKView.size()<k) {
                topKView.add(view);
            } else {
                if(freqEntry.getValue()>topKView.peek().freq) {
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
    public List<Integer> topKViewPhotoQS(int[] photoIds, int k) {
        if(photoIds==null || photoIds.length<1) return new ArrayList<>();

        Map<Integer, Integer> freqMap = new HashMap<>();
        for(int id: photoIds) {
            int freq = freqMap.getOrDefault(id, 0)+1;
            freqMap.put(id, freq);
        }
        Photo[] list = new Photo[freqMap.size()];
        int index = 0;
        for (int key : freqMap.keySet()) {
            list[index++] = new Photo(key, freqMap.get(key));
        }
        kthHelper(list, list.length - k + 1, 0, list.length - 1);
        List<Integer> result = new ArrayList<>();
        index--;
        while (k > 0) {
            result.add(list[index--].id);
            k--;
        }
        return result;
    }

    private Photo kthHelper(Photo[] nums, int kthSmallest, int left, int right) {
        if (left == right) {
            return nums[left];
        }
        int pos = partition(nums, left, right);
        if (pos + 1 == kthSmallest) {
            return nums[pos];
        } else if (pos + 1 < kthSmallest) {
            return kthHelper(nums, kthSmallest, pos + 1, right);
        } else {
            return kthHelper(nums, kthSmallest, left, pos - 1);
        }
    }

    private int partition(Photo[] nums, int left, int right) {
        int l = left, r = right;
        Photo p = nums[l];
        int pivot = nums[l].freq;
        while (l < r) {
            while (l < r && nums[r].freq >= pivot) {
                r--;
            }
            nums[l] = nums[r];
            while (l < r && nums[l].freq <= pivot) {
                l++;
            }
            nums[r] = nums[l];
        }
        nums[l] = p;
        return l;
    }
}
