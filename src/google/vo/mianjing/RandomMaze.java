package google.vo.mianjing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

// https://codeantenna.com/a/pMhZ82zDrJ
public class RandomMaze {
    int len = 11; //迷宫长度
    int wid = 11; //迷宫宽度
    char wall = '■'; //代表墙
    char blank = '○'; //代表空地
    char[][] maze; //迷宫
    boolean[][] visit; //用来标记某一格是否被访问过
    Node start = new Node(0, 0); //开始节点
    Node exit = new Node(len - 1, wid - 1); //出口，其实现在也没什么用，因为没有交互只是生成了一个迷宫而已
    Node cur; //当前格
    Node next; //下一格
    Stack<Node> path = new Stack(); //表示路径的栈

    int[][] adj = {
            {0, 2}, {0, -2}, {2, 0}, {-2, 0}
    }; //用来计算邻接格

    class Node {
        int x, y;
        public Node() {
        }
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "Node [x=" + x + ", y=" + y + "]";
        }
    }

    void init() {
        maze = new char[len][wid];
        visit = new boolean[len][wid];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < wid; j++) {
                maze[i][j] = wall;
                visit[i][j] = false;
            }
        }
        visit[start.x][start.y] = true;
        maze[start.x][start.y] = blank;
        cur = start; //将当前格标记为开始格
    }

    void printMaze() {
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < wid; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("==========================================");
    }

    void makeMaze() {
        path.push(cur); //将当前格压入栈
        while (!path.empty()) {
            Node[] adjs = notVisitedAdj(cur);//寻找未被访问的邻接格
            if (adjs.length == 0) {
                cur = path.pop();//如果该格子没有可访问的邻接格，则跳回上一个格子
                continue;
            }
            next = adjs[new Random().nextInt(adjs.length)]; //随机选取一个邻接格
            int x = next.x;
            int y = next.y;
//如果该节点被访问过，则回到上一步继续寻找
            if (visit[x][y]) {
                cur = path.pop();
            } else//否则将当前格压入栈，标记当前格为已访问，并且在迷宫地图上移除障碍物
            {
                path.push(next);
                visit[x][y] = true;
                maze[x][y] = blank;
                maze[(cur.x + x) / 2][(cur.y + y) / 2] = blank; //移除当前格与下一个之间的墙壁
                cur = next;//当前格等于下一格
            }
        }
    }

    boolean allVisited(Node[] ns) {
        for (Node n : ns) {
            if (!visit[n.x][n.y])
                return false;
        }
        return true;
    }

    Node[] notVisitedAdj(Node node) {
        List<Node> list = new ArrayList();
        for (int i = 0; i < adj.length; i++) {
            int x = node.x + adj[i][0];
            int y = node.y + adj[i][1];
            if (x >= 0 && x < len && y >= 0 && y < wid) {
                if (!visit[x][y])
                    list.add(new Node(x, y));
            }
        }
        Node[] a = new Node[list.size()];
        for (int i = 0; i < list.size(); i++) {
            a[i] = list.get(i);
        }
        return a;
    }

    public static void main(String[] args) {
        RandomMaze m = new RandomMaze();
        m.init();
        m.makeMaze();
        m.printMaze();
    }
}