package uber.phone;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MeetingRooms2 {
    Map<Integer, TreeMap<Integer, Integer>> rooms;
    int n;
    public MeetingRooms2(int n) {
        this.rooms = new HashMap<>();
        for (int i = 0; i < n; i++) {
            rooms.put(i, new TreeMap<>());
        }
        this.n = n;
    }

    public int findRoom(int[] time) {
        for (int room : rooms.keySet()) {
            boolean validRoom = validRoom(rooms.get(room), time);
            if (validRoom) {
                return room;
            }
        }
        throw new IllegalStateException("No rooms available");
    }

    private boolean validRoom(TreeMap<Integer, Integer> map, int[] time) {
        int start = time[0], end = time[1];
        Map.Entry<Integer, Integer> floor = map.floorEntry(start);
        if (floor != null) {
            if (floor.getValue() > start) {
                return false;
            }
        }
        Map.Entry<Integer, Integer> ceil = map.ceilingEntry(start);
        if (ceil != null) {
            if (ceil.getKey() < end) {
                return false;
            }
        }
        map.put(start, end);
        return true;
    }

    public static void main(String[] args) {
        try {
            MeetingRooms2 rooms = new MeetingRooms2(3);
            System.out.println(rooms.findRoom(new int[]{1,3}));
            System.out.println(rooms.findRoom(new int[]{2,4}));
            System.out.println(rooms.findRoom(new int[]{2,5}));
            System.out.println(rooms.findRoom(new int[]{3,5}));
            System.out.println(rooms.findRoom(new int[]{5,15}));
            System.out.println(rooms.findRoom(new int[]{4,5}));
            System.out.println(rooms.findRoom(new int[]{5,6}));
            System.out.println(rooms.findRoom(new int[]{5,16}));
            System.out.println(rooms.findRoom(new int[]{1,6}));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
