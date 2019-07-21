package dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindDuplicateFiles2 {
    public List<List<String>> findDuplicatesOpt(String path) throws Exception {
        Map<Long, List<String>> filesMap = new HashMap<>();
        Map<String, List<String>> hashFileMap = new HashMap<>();
        dfsSearchAllFiles(filesMap, path);
        List<List<String>> result = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : filesMap.entrySet()) {
            if (entry.getValue().size() >= 2) {
                for (String file : entry.getValue()) {
                    String key = fileHashFunc(file);
                    hashFileMap.putIfAbsent(key, new ArrayList<>());
                    hashFileMap.get(key).add(file);
                }
            }
        }
        for (Map.Entry<String, List<String>> entry : hashFileMap.entrySet()) {
            if (entry.getValue().size() >= 2) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    private String fileHashFunc(String file) throws Exception {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int readBytes = -1;
            byte[] buffer = new byte[1024];
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while ((readBytes = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, readBytes);
            }
            byte[] nums = md5.digest();
            return convertToHexString(nums);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private String convertToHexString(byte[] nums) {
        int prime = 11311331, a = 31, hashValue = 1;
        for (byte b : nums) {
            hashValue = (hashValue * a + b) % prime;
        }
        return Integer.toHexString(hashValue);
    }

    private void dfsSearchAllFiles(Map<Long, List<String>> filesMap, String path) {
        File f = new File(path);
        if (f.isFile()) {
            long size = f.length();
            filesMap.putIfAbsent(size , new ArrayList<>());
            filesMap.get(size).add(path);
            return;
        }
        if (f.isDirectory()) {
            for (String next : f.list()) {
                dfsSearchAllFiles(filesMap, path + "/" + next);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        FindDuplicateFiles2 solver = new FindDuplicateFiles2();
        List<List<String>> result = solver.findDuplicatesOpt("/Users/yunzou/Desktop");
        for (List<String> list : result) {
            System.out.println(list.toString());
        }
    }
}
