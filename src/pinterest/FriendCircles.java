package pinterest;

import java.util.*;

public class FriendCircles {
    public static void main(String[] args) {
        String[] friendshipsInput = {"1,2","1,3","1,6","2,4"};
        String[] employeesInput = {"1.Rich.Engineering", "2.Er.HR","3.Mo,Bus",
                "4.Di.Engineering", "6.Ca.Engineering", "9.La.Dir"};
    }

    public Map<Integer, List<Integer>> findFriend(String[] friendshipsInput) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (String item : friendshipsInput) {
            String[] arr = item.split(",");
            int from = Integer.parseInt(arr[0]), to = Integer.parseInt(arr[1]);
            if (!map.containsKey(from)) {
                map.put(from, new ArrayList<>());
            }
            map.get(from).add(to);
        }
        return map;
    }


}
