package reddit.New2026;

import java.util.*;

public class MergerWithAPI {
    public static class Item {
        int r, c;
        Chatter.Message msg;
        public Item(Chatter.Message msg , int r, int c) {
            this.c = c;
            this.r = r;
            this.msg = msg;
        }
    }

    public static class Merger {
        // Per instance: the constructor loads THIS Merger's messages.
        Chatter chatter = new Chatter();

        public Merger(List<Chatter.Message> msgs) {
            chatter.load(msgs);
        }

        public List<Chatter.Message> mergeMessages(List<Long> ids) {
            List<List<Chatter.Message>> lists = new ArrayList<>();
            for (long id : ids) {
                List<Chatter.Message> list = chatter.getMessages(id);
                if (list.isEmpty()) continue;
                lists.add(list);
            }

            return merger(lists);
        }

        private static List<Chatter.Message> merger(List<List<Chatter.Message>> lists) {
            if (lists.isEmpty()) return Collections.emptyList();

            List<Chatter.Message> result = new ArrayList<>();
            PriorityQueue<Item> pq = new PriorityQueue<>((a, b) -> {
                if (a.msg.id != b.msg.id) return (Double.compare(a.msg.id, b.msg.id));
                return Integer.compare(b.msg.version, a.msg.version);
            });

            for (int r = 0; r < lists.size(); r++) {
                pq.add(new Item(lists.get(r).get(0), r, 0));
            }
            double prevId = Double.MIN_VALUE;
            while (!pq.isEmpty()) {
                Item cur = pq.poll();
                if (result.isEmpty() || cur.msg.id != prevId) {
                    result.add(cur.msg);
                }
                prevId = cur.msg.id;
                int size = lists.get(cur.r).size();
                if (cur.c + 1 < size) {
                    pq.add(new Item(lists.get(cur.r).get(cur.c + 1), cur.r, cur.c + 1));
                }
            }
            return result;
        }
    }

    public static void main(String[] args) {

    }

}
