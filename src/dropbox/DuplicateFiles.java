package dropbox;

import java.util.*;

public class DuplicateFiles {
/*
    input "/foo"
    - /foo
      - /images
        - /foo.png  <------|
      - /temp                   | same file contents
        - /baz                   |
          - /that.foo  <- - - |-- |
      - /bar.png  <------- |     |
      - /file.tmp  <------------| same file contents
      - /other.temp  <--------|
      - /blah.txt


    Output:
    [
       ['/foo/bar.png', '/foo/images/foo.png'],
       ['/foo/file.tmp', '/foo/other.temp', '/foo/temp/baz/that.foo']
    ]
*/
    public static List<List<String>> findDuplicateFiles(String[][] paths) {
        Map<String, List<String>> map = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        for (String[] path : paths) {
            String from = path[0], to = path[1];
            map.putIfAbsent(from, new ArrayList<>());
            map.get(from).add(to);
            inDegree.putIfAbsent(from, 0);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        }
        List<List<String>> result = new ArrayList<>();
        List<String> files = new ArrayList<>();
        for (String root : inDegree.keySet()) {
            if (inDegree.get(root) == 0) {
                dfsSearchAll(root, root ,files, map);
            }
        }
        System.out.println(files.toString());
        for (int i = 0; i < files.size() - 1; i++) {
            for (int j = i + 1; j < files.size(); j++) {
                String aPath = files.get(i), bPath = files.get(j);
                // check file size
                // check md5 whole file or parts of file if too large
                // check file byte by byte
                if (fileSize(aPath) == fileSize(bPath) && md5CheckSum(aPath) == md5CheckSum(bPath)) {

                }
            }
        }
        return result;
    }

    private static int md5CheckSum(String aPath) {
        return 0;
    }

    private static int fileSize(String path) {
        return 0;
    }

    private static void dfsSearchAll(String path, String cur, List<String> files, Map<String, List<String>> map) {
        if (!map.containsKey(cur)) {
            files.add(path);
            return;
        }
        for (String nextPath : map.get(cur)) {
            dfsSearchAll(path + "/" + nextPath, nextPath, files, map);
        }
    }

    public static void main(String[] args) {
        String[][] paths = new String[][]{{"foo", "images"},{"images","foo.png"},{"foo", "temp"},{"temp", "baz"},
                {"baz", "that.foo"}, {"foo", "bar.png"}, {"foo", "file.tmp"}, {"foo", "other.temp"}, {"foo", "blah.txt"}};
        List<List<String>> result = findDuplicateFiles(paths);
    }
}
