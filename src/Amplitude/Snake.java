package Amplitude;

import java.util.Random;
import java.util.Scanner;

public class Snake {
    int[][] board;
    int snakeHeadR, snakeHeadC;
    int len;
    int rows, cols;
    boolean gameOver;
    int dirR, dirC;
    public Snake(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new int[rows][cols];
        this.snakeHeadR = rows / 2;
        this.snakeHeadC = cols / 2;
        this.len = 1;
        board[snakeHeadR][snakeHeadC] = len;
        this.gameOver = false;
        this.dirR = 0;
        this.dirC = -1;
        placeFood();
    }

    public void drawSnakeBoard() {
        for (int i = 0; i < rows; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                String str = board[i][j] <= 9 && board[i][j] >= 0 ? " | " + board[i][j] : " |" + board[i][j];
                sb.append(str);
            }
            sb.append(" |");
            System.out.println(sb);
        }
        System.out.println();
    }

    public void keyPressed(int dir) {
        int[][] dirs = {{1,0},{0,1},{-1,0},{0,-1}};
        dir--;
        if (dir < 4 && dir >= 0){
            moveSnake(dirs[dir][0], dirs[dir][1]);
        } else {
            System.out.println("Not valid input: 1 to 4");
        }
    }

    public void timeFried() {
        while (!gameOver) {
            moveSnake(dirR, dirC);
            drawSnakeBoard();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveSnake(int dr, int dc) {
        this.dirR = dr;
        this.dirC = dc;
        int newHeadR = snakeHeadR + dr;
        int newHeadC = snakeHeadC + dc;
        if (newHeadR >= rows || newHeadR < 0 || newHeadC >= cols || newHeadC < 0) {
            // hit boundary
            gameOver();
        } else if (board[newHeadR][newHeadC] > 0) {
            // hit itself
            gameOver();
        } else if (board[newHeadR][newHeadC] < 0) {
            // eat food
            board[newHeadR][newHeadC] = board[snakeHeadR][newHeadC] + 1;
            snakeHeadR = newHeadR;
            snakeHeadC = newHeadC;
            len++;
            placeFood();
        } else {
            // normal move
            board[newHeadR][newHeadC] = board[snakeHeadR][snakeHeadC] + 1;
            snakeHeadR = newHeadR;
            snakeHeadC = newHeadC;
            removeTail();
        }
    }

    public void gameOver() {
        gameOver = true;
    }

    public void placeFood() {
        // random select a cell to make it -1
        Random random = new Random();
        while (true) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (board[r][c] == 0) {
                board[r][c] = -1;
                break;
            }
        }
    }

    public void removeTail() {
        // all snake shrink by 1
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] > 0) {
                    board[i][j]--;
                }
            }
        }
    }

    public static void main(String[] args) {
        int rows = 10, cols = 10;
        Snake snake = new Snake(rows, cols);
        while (!snake.gameOver) {
            snake.timeFried();
            Scanner keyboard = new Scanner(System.in);
            int dir = keyboard.nextInt();
        }

    }

}
