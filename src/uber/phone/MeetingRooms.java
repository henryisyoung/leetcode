package uber.phone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingRooms {
    Map<Integer, List<int[]>> rooms;
    public MeetingRooms(int n) {
        this.rooms = new HashMap<>();
        for (int i = 0; i < n; i++) {
            rooms.put(i, new ArrayList<>());
        }
    }

    public int findRoom(int[] time) {
        for (int room : rooms.keySet()) {
            if (findPos(rooms, room, time)) {
                return room;
            }
        }
        throw new IllegalStateException("No available meeting rooms");
    }

    private boolean findPos(Map<Integer, List<int[]>> rooms, int room, int[] time) {
        int pos = 0;
        List<int[]> meetings = rooms.get(room);
        for (int[] meeting : meetings) {
            if (meeting[1] <= time[0]) {
                pos++;
            } else if (meeting[0] >= time[1]) {
                break;
            } else {
                return false;
            }
        }
        meetings.add(pos, time);
        return true;
    }

    public static void main(String[] args) {
        try {
            MeetingRooms rooms = new MeetingRooms(3);
            System.out.println(rooms.findRoom(new int[]{1,3}));
            System.out.println(rooms.findRoom(new int[]{2,4}));
            System.out.println(rooms.findRoom(new int[]{2,5}));
            System.out.println(rooms.findRoom(new int[]{3,5}));
            System.out.println(rooms.findRoom(new int[]{5,15}));
            System.out.println(rooms.findRoom(new int[]{4,5}));
            System.out.println(rooms.findRoom(new int[]{1,5}));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
