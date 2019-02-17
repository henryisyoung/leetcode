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
        public int compareTo(Buddy that) {
            return that.similarity - this.similarity;
        }
    }

    private List<Buddy> buddies;
    private Set<String> myWishList;

    public TravelBuddy(Set<String> myWishList, Map<String, Set<String>> friendsWishLists) {
        this.myWishList = myWishList;
        for (String friend : friendsWishLists.keySet()) {
            Set<String> friendsWishList = friendsWishLists.get(friend);
            Set<String> interSection = new HashSet<>(friendsWishList);
            interSection.removeAll(myWishList);
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
}
