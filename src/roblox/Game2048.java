package roblox;

public class Game2048 {
    int[][] solution(int[][] grid, String path) {
        for (int i = 0; i < path.length(); i++) {
            int dx = 0;
            int dy = 0;
            switch (path.charAt(i)) {
                case 'L':
                    dy = -1;
                    break;
                case 'R':
                    dy = 1;
                    break;
                case 'D':
                    dx = 1;
                    break;
                case 'U':
                    dx = -1;
                    break;
            }
            int[][] start = new int[4][2];
            if (path.charAt(i) == 'L' || path.charAt(i) == 'R') {
                for (int j = 0; j < 4; j++) {
                    start[j][0] = j;
                    start[j][1] = dy > 0 ? 3 : 0;
                }
            } else {
                for (int j = 0; j < 4; j++) {
                    start[j][0] = dx > 0 ? 3 : 0;
                    start[j][1] = j;
                }
            }
            for (int j = 0; j < 4; j++) {
                int boundX = start[j][0] + dx;
                int boundY = start[j][1] + dy;
                for (int k = 0; k < 4; k++) {
                    int posX = start[j][0];
                    int posY = start[j][1];
                    int value = grid[posX][posY];
                    if (value != 0) {
                        grid[posX][posY] = 0;
                        while ((posX + dx != boundX || posY + dy != boundY)
                                && grid[posX + dx][posY + dy] == 0) {
                            posX += dx;
                            posY += dy;
                        }
                        if ((posX + dx != boundX || posY + dy != boundY)
                                && grid[posX + dx][posY + dy] == value) {
                            grid[posX + dx][posY + dy] += value;
                            boundX = posX + dx;
                            boundY = posY + dy;
                        } else {
                            grid[posX][posY] = value;
                        }
                    }
                    start[j][0] -= dx;
                    start[j][1] -= dy;
                }
            }
        }
        return grid;
    }

}
