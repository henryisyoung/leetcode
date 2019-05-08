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
    public void noCheckout(String[][] records) {
        Map<String, Integer> map = new HashMap<>();
        Set<String> exitWithoutBadge = new HashSet<>();
        Set<String> enterWithoutBadge = new HashSet<>();

        for(String[] record: records) {
            Integer prev = map.get(record[0]);
            if(record[1].equals("enter")) {
                if(prev != null && prev == 1) {
                    exitWithoutBadge.add(record[0]);
                }
                prev = 1;
            } else {
                if(prev == null || prev == 0) {
                    enterWithoutBadge.add(record[0]);
                }
                prev = 0;
            }
            map.put(record[0], prev);
        }

        for(String person: map.keySet()){
            if(map.get(person) > 0) {
                exitWithoutBadge.add(person);
            }
        }

        printSet("enter without badge " , enterWithoutBadge);
        printSet("exit without badge " , exitWithoutBadge);
    }

    static void printSet(String s, Set<String> set) {
        System.out.println(s);
        for(String i: set){
            System.out.println(i + " ");
        }
        System.out.println();
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
        solver.noCheckout(records);
    }
}
