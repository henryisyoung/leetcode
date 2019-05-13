package pinterest;

import leetcode.ListNode;

import java.util.*;

public class BadgeCheck {
    // 1. All employees who didn't use their badge while exiting the room – they recorded an enter without a matching exit.
    // 2. All employees who didn't use their badge while entering the room  – they recorded an exit without a matching enter. 
    // 3. We want to find employees who badged into our secured room unusually often. We have an unordered list of names
    // and access times over a single day. Access times are given as three or four-digit numbers using 24-hour time, such
    // as "800" or "2250".   Write a function that finds anyone who badged into the room 3 or more times in a 1-hour period,
    // and returns each time that they badged in during that period. (If there are multiple 1-hour periods where this was true,
    // just return the first one.) 
    public void badgeCheck(String[][] records) {
        Map<String, String> map = new HashMap<>();
        Set<String> noCheckIn = new HashSet<>(), noCheckOut = new HashSet<>();
        for (String[] record : records) {
            String name = record[0], action = record[1];
            String prevAction = map.get(name);
            if (action.equals("enter")) {
                if (prevAction != null && prevAction.equals("enter")) {
                    noCheckOut.add(name);
                }
            } else {
                if (prevAction == null || prevAction.equals("exit")) {
                    noCheckIn.add(name);
                }
            }
            map.put(name, action);
        }
        for (String name : map.keySet()) {
            if (map.get(name).equals("enter")) {
                noCheckOut.add(name);
            }
        }
        System.out.println("noCheckIn : " + noCheckIn.toString());
        System.out.println("noCheckOut : " + noCheckOut.toString());
    }

    public List<List<Integer>> securityCheck(String[][] records) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (String[] record : records) {
            String name = record[0];
            int time = Integer.parseInt(record[1]);
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<>());
            }
            map.get(name).add(time);
        }
        List<List<Integer>> result = new ArrayList<>();
        for (String name : map.keySet()) {
            List<Integer> times = map.get(name);
            if (times.size() < 3) {
                continue;
            } else {
                Collections.sort(times);
                int j = 0;
                for (int i = 0; i < times.size(); i++) {
                    int index = oneHour(times, i);
                    if(index - i >= 3) {
                        List<Integer> temp = new ArrayList<>();
                        while( i < index) {
                            temp.add(times.get(i));
                            i++;
                        }
                        result.add(temp);
                        break;
                    }
                }
            }
        }
        return result;
    }

    int oneHour(List<Integer> list, int startIndex) {
        int endVal = list.get(startIndex) + 100;
        int endPos = startIndex;
        while(endPos < list.size()) {
            if(list.get(endPos) <= endVal) {
                endPos++;
            } else {
                break;
            }
        }
        return endPos;
    }

    public static void main(String[] args) {
        String[][] records = new String[][]{
                {"Martha","exit"},
                {"Paul","enter"},
                {"Martha","enter"},
                {"Martha","exit"},
                {"Jennifer","enter"},
                {"Paul","enter"},
                {"Curtis","enter"},
                {"Paul","exit"},
                {"Martha","enter"},
                {"Martha","exit"},
                {"Jennifer", "exit"},
        };
        BadgeCheck solver = new BadgeCheck();
//        solver.badgeCheck(records);

        String[][] records2 = new String[][]{
                {"Paul","1355"},
                {"Jennifer","1910"},
                {"John","830"},
                {"Paul","1315"},
                {"John","835"},
                {"Paul","1405"},
                {"Paul","1630"},
                {"John","855"},
                {"John","915"},
                {"John","930"},
                {"Jennifer","1335"},
                {"Jennifer","730"},
                {"John","1630"},};
        List<List<Integer>> result = solver.securityCheck(records2);
        System.out.println(result.toString());
    }
}
