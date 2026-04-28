package snowflake;

import java.util.*;

public class FileSystem {

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
            int index = path.lastIndexOf("/");
            String fileName = path.substring(index + 1);
            return Arrays.asList(fileName);
        }
        if (dirs.containsKey(path)) {
            Set<String> set = dirs.get(path);
            for (String sub : set) {
                int index = sub.lastIndexOf("/");
                String name = sub.substring(index + 1);
                result.add(name);
            }
            Collections.sort(result);
            return result;
        }
        return result;
    }

    public void mkdir(String path) {
        if (dirs.containsKey(path)) return;
        int pos = path.lastIndexOf("/");

        if (pos == 0) {
            dirs.get("/").add(path);
            dirs.put(path, new HashSet<>());
            return;
        }

        String parent = path.substring(0, pos);
        if (!dirs.containsKey(parent)) {
            mkdir(parent);
        }

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
