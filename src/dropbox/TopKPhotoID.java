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

    private void kthHelper(Photo[] nums, int kthSmallest, int left, int right) {
        if (left == right) {
            return;
        }
        int pos = partition(nums, left, right);
        if (pos + 1 == kthSmallest) {
            return;
        } else if (pos + 1 < kthSmallest) {
            kthHelper(nums, kthSmallest, pos + 1, right);
        } else {
            kthHelper(nums, kthSmallest, left, pos - 1);
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


    public void findKth(int[] arr, int k) {
        finderHelper(arr, 0, arr.length - 1, arr.length - k + 1);
        System.out.println(Arrays.toString(arr));
    }

    private void finderHelper(int[] arr, int left, int right, int k) {
        if (left >= right) return;
        int pos = partiion(arr, left, right);
        if (pos + 1 == k) return;
        if (pos + 1 < k) {
            finderHelper(arr, pos + 1, right, k);
        } else {
            finderHelper(arr, left, pos - 1, k);
        }
    }

    private int partiion(int[] arr, int left, int right) {
        int l = left, r = right, pivot = arr[l];
        System.out.println(l + " " + r);
        while (l < r) {
            while (l < r && arr[r] >= pivot) {
                r--;
            }
            arr[l] = arr[r];
            while (l < r && arr[l] <= pivot) {
                l++;
            }
            arr[r] = arr[l];
        }
        arr[l] = pivot;
        return l;
    }

    public static void main(String[] args) {
        int[] arr = {1,6,3,5,4,0,2};
        int k = 2;
        TopKPhotoID solver = new TopKPhotoID();
        solver.findKth(arr, k);
    }
}
