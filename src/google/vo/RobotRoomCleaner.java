package google.vo;

import java.util.HashSet;
import java.util.Set;

public class RobotRoomCleaner {
    public void cleanRoom(Robot robot) {
        Set<String> visited = new HashSet<>();
        dfsClean(robot, 0, 0, 0, visited);
    }

    private void dfsClean(Robot robot, int r, int c, int cur, Set<String> visited) {
        String key = r + "&" + c;
        if (visited.contains(key)) {
            return;
        }
        visited.add(key);
        robot.clean();
        int[][] dirs = {{1,0 },{0,-1},{-1,0}, {0,1}};

        for (int i = 0; i < 4; i++) {
            int nr = r + dirs[cur][0], nc = c + dirs[cur][1];
            if (robot.move()) {
                dfsClean(robot, nr, nc, cur, visited);
                backTrack(robot);
            }
            robot.turnRight();
            cur++;
            cur %= 4;
        }
    }

    private void backTrack(Robot robot) {
        robot.turnLeft();
        robot.turnLeft();
        robot.move();
        robot.turnLeft();
        robot.turnLeft();
    }
}
