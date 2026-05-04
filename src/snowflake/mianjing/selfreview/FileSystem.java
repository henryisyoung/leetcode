package snowflake.mianjing.selfreview;

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
        if (files.containsKey(path)) {
            int index = path.lastIndexOf("/");
            String name = path.substring(index + 1);
            return Arrays.asList(name);
        }
        if (dirs.containsKey(path)) {
            Set<String> set = dirs.get(path);
            List<String> result = new ArrayList<>();
            for (String child : set) {
                int index = child.lastIndexOf("/");
                String name = child.substring(index + 1);
                result.add(name);
            }
            Collections.sort(result);
            return result;
        }

        return new ArrayList<>();
    }

    public void mkdir(String path) {
        if(dirs.containsKey(path)) {
            return;
        }

        int index = path.lastIndexOf("/");
        String dir = path.substring(index);
        if(index == 0) {
            dirs.get("/").add(dir);
            dirs.put(path, new HashSet<>() );
            return;
        }

        String parent = path.substring(0, index);
        mkdir(parent);

        dirs.get(parent).add(path);
        dirs.put(path, new HashSet<>());
    }

    public void addContentToFile(String filePath, String content) {
        if (files.containsKey(filePath)) {
            String newContent = files.get(filePath) + content;
            files.put(filePath, newContent);
        } else {
            mkdir(filePath);
            files.put(filePath, content);
        }
    }

    public String readContentFromFile(String filePath) {
        return files.get(filePath);
    }
}
