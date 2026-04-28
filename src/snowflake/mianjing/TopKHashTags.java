package snowflake.mianjing;

import java.util.*;

/*
Problem Requirements
You are provided with a list called events. Each event is an array containing a [userId, hashtag].

We determine a hashtag's popularity by counting the number of unique users who posted it.

Note: If a single user posts the same hashtag multiple times, it only counts as one point of popularity for that hashtag.
Your task is to return the top k hashtags. You must sort the results using these rules:

Most popular hashtags come first.
If two hashtags have the same popularity, pick the one that is smaller alphabetically (lexicographically).
If k is greater than the total number of unique hashtags, simply return all of them in the correct order.

Sample Cases
Example 1:

Input: events = [["u1","#ai"],["u1","#ai"],["u2","#ai"],["u2","#ml"],["u3","#ml"],["u4","#db"],["u4","#db"]], k = 2

Output: ["#ai","#ml"]

Explanation: Both #ai and #ml have a popularity score of 2 (distinct users). Because the score is tied, #ai comes before #ml alphabetically.

Example 2:

Input: events = [["alice","#x"],["bob","#y"],["alice","#y"],["alice","#z"]], k = 5

Output: ["#y","#x","#z"]

Explanation: The popularity scores are:

#y: 2 users
#x: 1 user
#z: 1 user
Input Limits
0 <= events.length <= 10^5
events[i].length == 2
1 <= userId.length, hashtag.length <= 50
1 <= k <= 10^4
 */
public class TopKHashTags {

    static class Entry {
        String tag;
        int count;
        public Entry(int count, String tag) {
            this.count = count;
            this.tag = tag;
        }
    }

    int k;
    Map<String, Entry> map;
    TreeSet<Entry> set;

    public TopKHashTags(int k) {
        this.k = k;
        this.map = new HashMap<>();
        this.set = new TreeSet<>((a, b) -> {
            if (a.count != b.count) return b.count - a.count;
            return a.tag.compareTo(b.tag);
        });
    }

    public void addTag(String[] event) {
        String userId = event[0], tag = event[1];
        if (!map.containsKey(userId)) {
            Entry entry = new Entry(1, tag);
            map.put(userId, entry);
            set.add(entry);
        } else {
            Entry entry = map.get(userId);
            set.remove(entry);
            entry.count++;
            set.add(entry);
        }
    }

    public List<String> findTopK() {
        int limit = k;
        List<String> result = new ArrayList<>();


        return result;
    }
}
