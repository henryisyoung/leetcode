package google;

import java.util.*;

public class findBike {
    static class Location {
        int r, c;

        public Location(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    static class PersonBikePair {
        Location people, bike;
        int dist;

        public PersonBikePair(Location people, Location bike) {
            this.bike = bike;
            this.people = people;
            this.dist = Math.abs(people.r - bike.r) + Math.abs(people.c - bike.c);
        }
    }

    public static List<Location[]> assignBikes(char[][] grid) {
        List<Location[]> result = new ArrayList<>();
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return result;
        }
        int rows = grid.length, cols = grid[0].length;
        List<Location> people = new ArrayList<>(), bikes = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'p') {
                    people.add(new Location(i, j));
                }
                if (grid[i][j] == 'b') {
                    bikes.add(new Location(i, j));
                }
            }
        }

        PriorityQueue<PersonBikePair> pq = new PriorityQueue<>(people.size() * bikes.size(), new Comparator<PersonBikePair>() {
            @Override
            public int compare(PersonBikePair o1, PersonBikePair o2) {
                return o1.dist - o2.dist;
            }
        });
        for (Location pl : people) {
            for (Location bl : bikes) {
                pq.add(new PersonBikePair(pl, bl));
            }
        }
        while (!pq.isEmpty()) {
            PersonBikePair cur = pq.poll();
            Location pl = cur.people, bl = cur.bike;
            if (grid[pl.r][pl.c] == 'p' && grid[bl.r][bl.c] == 'b') {
                Location[] pair = new Location[2];
                pair[0] = pl;
                pair[1] = bl;
                result.add(pair);
                grid[pl.r][pl.c] = '#';
                grid[bl.r][bl.c] = '#';
            }
        }
        return result;
    }

    public static void main(String[] args) {
        char[][] grid = {
                {'p', 'o', 'p', 'o', 'o'},
                {'o', 'o', 'o', 'o', 'o'},
                {'b', 'o', 'o', 'o', 'b'},
                {'b', 'o', 'o', 'o', 'p'}
        };

        List<Location[]> results = assignBikes(grid);
        for (Location[] cur : results) {
            System.out.println(cur[0].r + " " + cur[0].c + " " + cur[1].r + " " + cur[1].c);
        }

    }
}