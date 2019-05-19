//package twitter;
//
//import java.util.*;
//
//public class FileSystem{
//    //DesignInMemoryFileSystem https://www.cnblogs.com/grandyang/p/6944331.html
//    Map<String, List<String>> dirs, files;
//    public FileSystem() {
//        this.dirs = new HashMap<>();
//        this.files = new HashMap<>();
//        dirs.put("/", new ArrayList<>());
//    }
//
//    public List<String> ls(String path) {
//        if (files.containsKey(path)) {
//            String[] arr = path.split("/");
//            List<String> result = new ArrayList<>();
//            result.add(arr[arr.length - 1]);
//            return result;
//        }
//        List<String> list = dirs.get(path);
//        return list;
//    }
//
//    public void mkdir(String path) {
//        String[] arr = path.split("/");
//
//    }
//
//    public void addContentToFile(String filePath, String content) {
//
//    }
//
//    public String readContentFromFile(String filePath) {
//
//    }
//}
