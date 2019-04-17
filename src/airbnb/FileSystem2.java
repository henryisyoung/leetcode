package airbnb;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 什么是callback
 * 2. 如何拆分成各个path
 */
public class FileSystem2 {
    Map<String, Node> pathMap;
    Map<String, Runnable> callbackMap;

    private class Node {
        int val;
        String childPath;
        public Node(int val, String childPath) {
            this.val = val;
            this.childPath = childPath;
        }
    }

    public FileSystem2() {
        this.pathMap = new HashMap<>();
        this.callbackMap = new HashMap<>();
        pathMap.put("", new Node(0, ""));
    }

    public boolean create(String path, int val) {
        if (pathMap.containsKey(path)) {
            return false;
        }
        int pos = path.lastIndexOf("/");
        String childPath = path.substring(0, pos);
        if (!pathMap.containsKey(childPath)) {
            return false;
        }
        pathMap.put(path, new Node(val, childPath));
        return true;
    }

    public boolean set(String path, int val) {
        if (!pathMap.containsKey(path)) {
            return false;
        }
        Node n = pathMap.get(path);
        n.val = val;
        pathMap.put(path, n);

        String curPath = path;
        while (curPath.length() > 0) {
            if (callbackMap.containsKey(curPath)) {
                callbackMap.get(curPath).run();
            }
            curPath = pathMap.get(curPath).childPath;
        }
        return true;
    }

    public boolean watch(String path, Runnable runnable) {
        callbackMap.put(path, runnable);
        return true;
    }

    public boolean watch(String path, final String alert) {
        callbackMap.put(path, new Runnable() {
            @Override
            public void run() {
                System.out.println(alert);
            }
        });
        return true;
    }

    public static void main(String[] args) {
        FileSystem2 solver = new FileSystem2();
        solver.create("/a",1);
//        System.out.println(solver.get("/a"));
        solver.create("/a/b",2);
        String[] patha = {"/a","/a/b"};
        for (final String path : patha) {
            solver.watch(path, new Runnable() {
                @Override
                public void run() {
                    System.out.println("asdasdasdsa   " + path);
                }
            });
        }
        solver.set("/a/b", 8);
    }
}
