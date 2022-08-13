package Amplitude;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class FlightSimulation {
    public static List<String> flightSimulation(String[][] flights) {
        List<String> result = new ArrayList<>();
        if (flights == null || flights.length == 0)
            return result;

        PriorityQueue<Flight> pq = new PriorityQueue<>((a, b) -> {
          if (a.landTime != b.landTime) {
              return a.landTime - b.landTime;
          }
          return a.id.compareTo(b.id);
        });
        for (String[] flight : flights) {
            pq.add(new Flight(flight[0], Integer.parseInt(flight[1])));
        }

        int way1 = Integer.MIN_VALUE, way2 = Integer.MIN_VALUE;

        while (!pq.isEmpty()) {
            Flight flight = pq.poll();
            if(flight.landTime < Math.min(way1, way2)) {
                // no available ways
                result.add("postponed:" + flight.id);
                pq.add(new Flight(flight.id, flight.landTime + 10));
            } else {

                if (way1 < way2) {
                    way1 = flight.landTime + 5;
                } else {
                    way2 = flight.landTime + 5;
                }
                result.add("landed:" + flight.id);
            }
        }

        return result;
    }

    static class Flight {
        int landTime;
        String id;
        public Flight(String id, int landTime) {
            this.id = id;
            this.landTime = landTime;
        }
    }

    public static void main(String[] args) {
        String[][] flights = {
                {"1", "100"},
                {"2", "102"},
                {"3", "102"},
                {"4", "103"},
                {"5", "105"},
                {"6", "109"},
        };

        System.out.println(flightSimulation(flights));
    }
}
