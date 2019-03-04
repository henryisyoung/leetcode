package airbnb;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 什么是callback
 * 2. 如何拆分成各个path
 */
public class FileSystem {
    Map<String, Integer> pathMap;
    Map<String, Runnable> callbackMap;
    public FileSystem() {
        this.pathMap = new HashMap<>();
        this.callbackMap = new HashMap<>();
        this.pathMap.put("", 0);
    }

    public boolean create(String path, int value) {
        if (pathMap.containsKey(path)) {
            return false;
        }
        int lastSlashIndex = path.lastIndexOf("/");
        if (!pathMap.containsKey(path.substring(0, lastSlashIndex))) {
            return false;
        }
        pathMap.put(path, value);
        return true;
    }

    public boolean set(String path, int value) {
        if (!pathMap.containsKey(path)) {
            return false;
        }
        pathMap.put(path, value);
        // Trigger callbacks
        String curPath = path;

        while (curPath.length() > 0) {
            if (callbackMap.containsKey(curPath)) {
                callbackMap.get(curPath).run();
            }
            int lastSlashIndex = curPath.lastIndexOf("/");
            curPath = curPath.substring(0, lastSlashIndex);
        }
        return true;
    }

    public Integer get(String path) {
        return pathMap.get(path);
    }

    public boolean watch(String path, Runnable callback) {
        if (!pathMap.containsKey(path)) {
            return false;
        }

        callbackMap.put(path, callback);
        return true;
    }

    public static void main(String[] args) {
        FileSystem solver = new FileSystem();
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
