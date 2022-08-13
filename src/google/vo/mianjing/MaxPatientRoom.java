package google.vo.mianjing;

import java.util.*;

public class MaxPatientRoom {
    // https://leetcode.com/discuss/interview-question/1566515/GoogleorOnsiteorBusiest-meeting-room/1143254
    public List<Integer> maxPatientRoom(int[][] patients, int k) {
        List<Integer> res = new ArrayList<>();
        PriorityQueue<int[]> minHeapRoomsInUse = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<int[]> minHeapRoomsFree = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 0; i < k; i++) {
            minHeapRoomsFree.offer(new int[]{0, i});
        }
        int curStartTime = 0;
        int[] roomsPatientCount = new int[k];
        for (int[] patient : patients) {
            curStartTime = Math.max(curStartTime, patient[0]);
            while (!minHeapRoomsInUse.isEmpty() && minHeapRoomsInUse.peek()[0] <= curStartTime) {
                minHeapRoomsFree.offer(minHeapRoomsInUse.poll());
            }
            if (minHeapRoomsFree.isEmpty()) {
                int[] cur = minHeapRoomsInUse.poll();
                int time = cur[0];
                curStartTime = Math.max(curStartTime, time);
                minHeapRoomsFree.offer(cur);
            }
            int[] roomWithTime = minHeapRoomsFree.poll();
            int roomId = roomWithTime[1];
            roomsPatientCount[roomId]++;
            minHeapRoomsInUse.offer(new int[]{curStartTime + patient[1], roomId});
        }
        int maxPatient = 0;
        for (int i = 0; i < k; i++) {
            if (roomsPatientCount[i] > maxPatient) {
                maxPatient = roomsPatientCount[i];
            }
        }
        for (int i = 0; i < k; i++) {
            if (roomsPatientCount[i] == maxPatient) {
                res.add(i);
            }
        }
        return res;
    }

    static class Node{
        int time, id;
        boolean isStart;
        public Node(int time, int id, boolean isStart) {
            this.id = id;
            this.time = time;
            this.isStart = isStart;
        }
    }

    public static int findMaxRoom(int n, int[][] patients) throws Exception {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = 0; i < n; i++) {
            pq.add(i);
        }
        Map<Integer, Integer> patientToRoom = new HashMap<>();
        List<Node> nodes = new ArrayList<>();
        int id = 0;
        for (int[] patient : patients) {
            nodes.add(new Node(patient[0], id, true));
            nodes.add(new Node(patient[1], id++, false));
        }

        Collections.sort(nodes, (a, b) -> {
            if (a.time == b.time) {
                if (a.isStart) {
                    return 1;
                }
                return -1;
            }
            return a.time - b.time;
        });
        int max = -1;
        int maxRoom = 0;
        Map<Integer, Integer> roomFreq = new HashMap<>();
        for (Node node : nodes) {
            if (node.isStart) {
                if (pq.isEmpty()) {
                    throw new Exception("no more room");
                }
                int room = pq.poll();
                roomFreq.put(room, roomFreq.getOrDefault(room, 0) + 1);
                patientToRoom.put(node.id, room);
                if (roomFreq.get(room) > max) {
                    maxRoom = room;
                }
            } else {
                int patient = node.id;
                int freeRoom = patientToRoom.get(patient);
                patientToRoom.remove(patient);
                pq.add(freeRoom);
            }
        }

        return maxRoom;
    }

    public static void main(String[] args) {
        int n = 4;

        int[][] nums = {
                {1,5},
                {2,4},
                {7,9},
                {5,10},
                {8,11},
                {12,15},
                {112,115},
                {1112,1115},
                {9,11}
        };
        try {
            System.out.println(findMaxRoom(n, nums));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
