package uber.phone;

import java.util.*;

public class MeetingRooms3 {

    static class Meeting {
        int start, end;
        public Meeting(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "Meeting{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
    Map<Integer, List<Meeting>> rooms;
    int n;
    Deque<Meeting> lastMeetings;
    public MeetingRooms3(int n) {
        this.rooms = new HashMap<>();
        for (int i = 0; i < n; i++) {
            rooms.put(i, new ArrayList<>());
        }
        this.lastMeetings = new LinkedList<>();
        this.n = n;
    }

    public int findRoom(int[] time) {
        for (int roomId : rooms.keySet()) {
            if (validRoom(time, roomId, rooms)) {
                System.out.println(rooms.get(roomId));
                return roomId;
            }
        }
        throw new IllegalStateException("no rooms available");
    }

    private boolean validRoom(int[] time, int roomId, Map<Integer, List<Meeting>> rooms) {
        List<Meeting> meetings = rooms.get(roomId);
        List<Meeting> merged = new ArrayList<>();
        int start = time[0], end = time[1];
        Meeting newMeeting = new Meeting(start, end);
        int pos = 0;
        for (Meeting meeting : meetings) {
            if (meeting.end < start) {
                pos++;
                merged.add(meeting);
            } else if (meeting.start > end){
                merged.add(meeting);
            } else {
                if (meeting.end == start || meeting.start == end) {
                    newMeeting.start = Math.min(start, meeting.start);
                    newMeeting.end = Math.max(end, meeting.end);
                } else {
                    return false;
                }
            }
        }
        merged.add(pos, newMeeting);
        meetings.clear();
        rooms.put(roomId, merged);
        return true;
    }

    public static void main(String[] args) {
        try {
            MeetingRooms3 rooms = new MeetingRooms3(3);
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
