package airbnb;

import java.util.*;

public class TravelBuddy {
    class Buddy implements Comparable<Buddy> {
        String name;
        int similarity;
        Set<String> friendsWishList;

        public Buddy(String name, int similarity, Set<String> friendsWishList) {
            this.name = name;
            this.similarity = similarity;
            this.friendsWishList = friendsWishList;
        }

        @Override
        public String toString() {
            return name + " similarity " + similarity;
        }

        @Override
        public int compareTo(Buddy that) {
            return that.similarity - this.similarity;
        }
    }

    private List<Buddy> buddies;
    private Set<String> myWishList;

    public TravelBuddy(Set<String> myWishList, Map<String, Set<String>> friendsWishLists) {
        this.myWishList = myWishList;
        this.buddies = new ArrayList<>();
        for (String friend : friendsWishLists.keySet()) {
            Set<String> friendsWishList = friendsWishLists.get(friend);
            Set<String> interSection = new HashSet<>(friendsWishList);
            interSection.retainAll(myWishList);
            int similarity = interSection.size();
            if (similarity >= friendsWishList.size() / 2) {
                buddies.add(new Buddy(friend, similarity, friendsWishList));
            }
        }
    }

    public List<Buddy> getSortedBuddies() {
        Collections.sort(buddies);
        List<Buddy> result = new ArrayList<>(buddies);
        return result;
    }

    public List<String> recommendCities(int k) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (k > 0 && i < buddies.size()) {
            Set<String> friendList = new HashSet<>(buddies.get(i).friendsWishList);
            friendList.removeAll(myWishList);
            if (friendList.size() <= k) {
                result.addAll(friendList);
                k -= friendList.size();
                i++;
            } else {
                Iterator<String> it = friendList.iterator();
                while (k > 0) {
                    result.add(it.next());
                    k--;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Set<String> myWishList = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            char c = (char) (i + 'a');
            myWishList.add(String.valueOf(c));
        }
        System.out.println("myWishList" + myWishList.toString());
        String buddy1 = "buddy1", buddy2 = "buddy2";
        Map<String, Set<String>> friendsWishLists = new HashMap<>();
        Set<String> b1Set = new HashSet<>(), b2Set = new HashSet<>();
        b1Set.add("a");
        b1Set.add("b");
        b1Set.add("e");
        b1Set.add("f");

        b2Set.add("a");
        b2Set.add("c");
        b2Set.add("d");
        b2Set.add("g");
        friendsWishLists.put(buddy1, b1Set);
        friendsWishLists.put(buddy2, b2Set);

        TravelBuddy solver = new TravelBuddy(myWishList, friendsWishLists);
        List<Buddy> result = solver.getSortedBuddies();
        System.out.println(result.toString());

        List<String> kCities = solver.recommendCities(1);
        System.out.println("kCities " + kCities.toString());
    }
}
