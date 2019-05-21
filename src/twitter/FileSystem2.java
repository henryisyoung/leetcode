package twitter;

import java.util.*;

public class FileSystem2 {
    //DesignInMemoryFileSystem https://www.cnblogs.com/grandyang/p/6944331.html
    Map<String, Set<String>> dirs;
    Map<String, String> files;
    public FileSystem2() {
        this.dirs = new HashMap<>();
        this.files = new HashMap<>();
        dirs.put("/", new HashSet<>());
    }

    public List<String> ls(String path) {
        List<String> result = new ArrayList<>();
        if (files.containsKey(path)) {
            int index = path.lastIndexOf("/");
            result = new ArrayList<>();
            result.add(path.substring(index + 1));
            return result;
        }
        result.addAll(dirs.get(path));
        Collections.sort(result);
        return result;
    }

    public void mkdir(String path) {
        String[] arr = path.split("/");
        String dir = "";
        for (String t : arr) {
            if (t.equals("")) {
                continue;
            }
            if (dir.equals("")) {
                dir += "/";
            }
            if (!dirs.containsKey(dir)) {
                dirs.put(dir, new HashSet<>());
            }

            dirs.get(dir).add(t);
            if (dir.length() > 1) dir += "/";
            dir += t;
        }
        if (dir.length() > 0 && !dirs.containsKey(dirs)) {
            dirs.put(dir, new HashSet<>());
        }
    }

    public void addContentToFile(String filePath, String content) {
        int index = filePath.lastIndexOf("/");
        String dir = filePath.substring(0, index);
        String file = filePath.substring(index + 1);
        if (dir.equals("")) {
            dir = "/";
        }
        if (!dirs.containsKey(dir)) {
            mkdir(dir);
        }
        dirs.get(dir).add(file);
        files.put(filePath, files.getOrDefault(filePath, "") + content);
    }

    public String readContentFromFile(String filePath) {
        return files.get(filePath);
    }

    public static void main(String[] args) {
        FileSystem2 solver = new FileSystem2();
        System.out.println(solver.ls("/"));
        solver.mkdir("/a/b/c");
        solver.addContentToFile("/a/b/c/d","hello");
        System.out.println(solver.ls("/"));
    }
}
