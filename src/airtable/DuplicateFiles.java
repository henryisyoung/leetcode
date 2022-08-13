package airtable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateFiles {
    //https://www.1point3acres.com/bbs/thread-795507-1-1.html

    // list of names of immediate file and folder children
    public List<String> listFolder(String path) {
        return new ArrayList<>();
    }

    public boolean isFolder(String path) {
        return true;
    }

    public List<List<String>> findDuplicates(String path) throws Exception {
        List<List<String>> result = new ArrayList<>();
        if (!isFolder(path)) {
            return result;
        }
        Map<String, List<String>> map = new HashMap<>();
        dfsFindAllPath(path, map);
        for (List<String> val : map.values()) {
            result.add(val);
        }
        return result;
    }

    private void dfsFindAllPath(String path, Map<String, List<String>> map) throws Exception {
        if (!isFolder(path)) {
            String hash = buildFileHash(path);
            map.putIfAbsent(hash, new ArrayList<>());
            map.get(hash).add(path);
            return;
        }
        for (String next : listFolder(path)) {
            dfsFindAllPath(path + "/" + next, map);
        }
    }

    private String buildFileHash(String path) throws Exception {
        File f = new File(path);
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, bytesRead);
            }
            byte[] hashedBytes = md5.digest();
            return hashedBytes.toString();
        } catch (IOException ex) {
            throw new Exception("Could not generate hash from file", ex);
        }
    }
}
