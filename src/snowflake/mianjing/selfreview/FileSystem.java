package snowflake.mianjing.selfreview;

import java.util.*;

public class FileSystem {

    Map<String, String> files;
    Map<String, Set<String>> dirs;
    public FileSystem() {
        this.files = new HashMap<>();
        this.dirs = new HashMap<>();
        dirs.put("/", new HashSet<>());
    }

    public List<String> ls(String path) {
        if (files.containsKey(path)) {
            int index = path.lastIndexOf("/");
            String name = path.substring(index + 1);
            return Arrays.asList(name);
        } else if (dirs.containsKey(path)) {
            Set<String> set = dirs.get(path);
            List<String> list = new ArrayList<>();
            for (String p : set) {
                int index = p.lastIndexOf("/");
                String name = p.substring(index + 1);
                list.add(name);
            }
            Collections.sort(list);
            return list;
        }
        return new ArrayList<>();
    }

    public void mkdir(String path) {
        if (dirs.containsKey(path)) return;
        int index = path.lastIndexOf("/");
        if (index == 0) {
            dirs.get("/").add(path);
            dirs.put(path, new HashSet<>());
            return;
        }

        String parent = path.substring(0, index);
        mkdir(parent);
        dirs.get(parent).add(path);
        dirs.put(path, new HashSet<>());
    }

    public void addContentToFile(String filePath, String content) {
        if (files.containsKey(filePath)) {
            files.put(filePath, files.get(filePath) + content);
            return;
        }

        mkdir(filePath);
        files.put(filePath, content);

    }

    public String readContentFromFile(String filePath) {
        return files.get(filePath);
    }
}
