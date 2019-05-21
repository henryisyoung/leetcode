package twitter;

import java.util.*;

public class FileSystem{
    //DesignInMemoryFileSystem https://www.cnblogs.com/grandyang/p/6944331.html
    Map<String, Set<String>> dirs;
    Map<String, String> files;
    public FileSystem() {
        this.dirs = new HashMap<>();
        this.files = new HashMap<>();
        dirs.put("/", new HashSet<>());
    }

    public List<String> ls(String path) {
        List<String> result = new ArrayList<>();
        if (files.containsKey(path)) {
            int pos = path.lastIndexOf("/");
            String fileName = path.substring(pos + 1);
            result.add(fileName);
            return result;
        }
        result.addAll(dirs.get(path));
        Collections.sort(result);
        return result;
    }

    public void mkdir(String path) {
        String dir = "";
        for (String t : path.split("/")) {
            if (t.equals("")) continue;
            if (dir.equals("")) dir += "/";
            if (!dirs.containsKey(dir)) {
                dirs.put(dir, new HashSet<>());
            }
            dirs.get(dir).add(t);
            if (dir.length() > 1) dir += "/";
            dir += t;
        }
        if (dir.length() > 1 && !dirs.containsKey(dir)) {
            dirs.put(dir, new HashSet<>());
        }
    }

    public void addContentToFile(String filePath, String content) {
        int pos = filePath.lastIndexOf("/");
        String fileName = filePath.substring(pos + 1);
        String dir = filePath.substring(0, pos);
        if (dir.length() == 0) {
            dir = "/";
        }
        if (!dirs.containsKey(dir)) {
            mkdir(dir);
        }
        dirs.get(dir).add(fileName);
        files.put(filePath, files.getOrDefault(filePath, "") + content);
    }

    public String readContentFromFile(String filePath) {
        return files.get(filePath);
    }

    public static void main(String[] args) {
        FileSystem solver = new FileSystem();
        System.out.println(solver.ls("/"));
        solver.mkdir("/a/b/c");
        solver.addContentToFile("/a/b/c/d","hello");
        System.out.println(solver.ls("/"));
    }
}
