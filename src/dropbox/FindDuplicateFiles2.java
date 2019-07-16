package dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindDuplicateFiles2 {
    public List<List<String>> findDuplicatesOpt(String path) throws Exception{
        List<List<String>> result = new ArrayList<>();
        Map<Long, List<String>> sizeMap = new HashMap<>();
        dfsSearchFileSize(sizeMap, path);
        Map<String, List<String>> map = new HashMap<>();
        for (List<String> files : sizeMap.values()) {
            if (files.size() < 2) continue;
            for (String file : files) {
                String key = findFileHash(file);
                map.putIfAbsent(key, new ArrayList<>());
                map.get(key).add(file);
            }
        }
        System.out.println(map.keySet());
        for (List<String> files : map.values()) {
            if (files.size() >= 2) result.add(files);
        }
        return result;
    }

    private void dfsSearchFileSize(Map<Long, List<String>> sizeMap, String path) {
        File f = new File(path);
        if (f.isFile()) {
            Long size = f.length();
            if (!sizeMap.containsKey(size)) sizeMap.put(size, new ArrayList<>());
            sizeMap.get(size).add(path);
            return;
        }
        if (f.isDirectory()) {
            for (String next : f.list()) {
                dfsSearchFileSize(sizeMap, path + "/" + next);
            }
        }
    }

    private String findFileHash(String file) throws Exception {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int readBytes = -1;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((readBytes = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, readBytes);
            }
            byte[] bytes = md5.digest();
            return convertByteArrayToHexString(bytes);
        } catch (Exception e) {
            throw new Exception("Could not generate hash from file", e);
        }
    }

    private String convertByteArrayToHexString(byte[] arrayBytes) {
        int result = 1, prime = 105613;
        for (byte i : arrayBytes) {
            result = (31 * result + i)  % prime;
        }
        return Integer.toHexString(result);
    }

    public static void main(String[] args) throws Exception {
        FindDuplicateFiles2 solver = new FindDuplicateFiles2();
        List<List<String>> result = solver.findDuplicatesOpt("/Users/yunzo/Desktop");
        for (List<String> list : result) {
            System.out.println(list.toString());
        }
    }
}
