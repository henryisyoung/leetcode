package dropbox;

import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class BitTorentDownload {
    class Chunk{
        int start, end;
        public Chunk(int start, int end) {
            this.end = end;
            this.start = start;
        }
    }
    PriorityQueue<Chunk> pq;
    int size;
    TreeSet<Chunk> treeSet;
    public BitTorentDownload(int size) {
        this.size = size;
        this.pq = new PriorityQueue<>((a, b) -> (a.start - b.start));
        this.treeSet = new TreeSet<>((a, b) -> (a.start - b.start));
    }

    public void addBlock2(Chunk chunk) {
        Chunk floor = treeSet.floor(chunk);
        if (floor != null) {
            if (floor.end >= chunk.start) {
                chunk.start = Math.min(floor.start, chunk.start);
                chunk.end = Math.max(floor.end, chunk.end);
                treeSet.remove(floor);
            }
        }
        Chunk ceil =treeSet.higher(chunk);
        if (ceil != null) {
            if (chunk.end >= ceil.start) {
                chunk.start = Math.min(ceil.start, chunk.start);
                chunk.end = Math.max(chunk.end, ceil.end);
                treeSet.remove(ceil);
            }
        }
        treeSet.add(chunk);
    }

    public void addBlock(Chunk chunk) {
        pq.add(chunk);
        if (pq.size() > 1) {
            Chunk smallest = pq.poll();
            while (!pq.isEmpty() && pq.peek().start <= smallest.end) {
                Chunk next = pq.poll();
                smallest.end = Math.max(smallest.end, next.end);
            }
            pq.add(smallest);
        }
    }

    public boolean isDone() {
        return !pq.isEmpty() && pq.peek().start == 0 && pq.peek().end == size;
    }

    public boolean isFileDone(List<Chunk> chunks, int size) {
        if(chunks==null || chunks.size()==0) return false;
        Collections.sort(chunks, (a, b) -> (a.start - b.start));
        if(chunks.get(0).start != 0) return false;
        int end = chunks.get(0).end;
        for (int i = 1; i < chunks.size(); i++) {
            if (end < chunks.get(i).start) return false;
            end = Math.max(end, chunks.get(i).end);
        }
        return end == size;
    }
}
