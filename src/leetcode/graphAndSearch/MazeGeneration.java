package leetcode.graphAndSearch;

public class MazeGeneration {
    public static class GenerateRandomMaze {
        public int[][] maze(int n) {
            // Assumptions: n = 2 * k + 1, where k > = 0.
            int[][] maze = new int[n][n];
            // initialize the matrix as only (0,0) is corridor,
            // other cells are all walls at the beginning.
            // later we are trying to break the walls to form corridors.
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == 0 && j == 0) {
                        maze[i][j] = 0;
                    } else {
                        maze[i][j] = 1;
                    }
                }
            }
            generate(maze, 0, 0);
            return maze;
        }
        private void generate(int[][] maze, int x, int y) {
            // get a random shuffle of all the possible directions,
            // and follow the shuffled order to do DFS & backtrack.
            Dir[] dirs = Dir.values();
            shuffle(dirs);
            for (Dir dir: dirs) {
                // advance by two steps.
                int nextX = dir.moveX(x, 2);
                int nextY = dir.moveY(y, 2);
                if (isValidWall(maze, nextX, nextY)) {
                    // only if the cell is a wall(meaning we have not visited before),
                    // we break the walls through to make it corridors.
                    maze[dir.moveX(x, 1)][dir.moveY(y, 1)] = 0;
                    maze[nextX][nextY] = 0;
                    generate(maze, nextX, nextY);
                }
            }
        }
        // Get a random order of the directions.
        private void shuffle(Dir[] dirs) {
            for (int i = 0; i < dirs.length; i++) {
                int index = (int)(Math.random() * (dirs.length - i));
                Dir tmp = dirs[i];
                dirs[i] = dirs[i + index];
                dirs[i + index] = tmp;
            }
        }
        // check if the position (x,y) is within the maze and it is a wall.
        private boolean isValidWall(int[][] maze, int x, int y) {
            return x >= 0 && x < maze.length && y >= 0 && y < maze[0].length && maze[x][y] ==
                    1;
        }
        enum Dir {
            NORTH(-1, 0), SOUTH(1, 0), EAST(0, -1), WEST(0, 1);
            int deltaX;
            int deltaY;
            Dir(int deltaX, int deltaY) {
                this.deltaX = deltaX;
                this.deltaY = deltaY;
            }
            // move certain times of deltax.
            public int moveX(int x, int times) {
                return x + times * deltaX;
            }
            // move certain times of deltaY.
            public int moveY(int y, int times) {
                return y + times * deltaY;

            }
        }
    }

}
