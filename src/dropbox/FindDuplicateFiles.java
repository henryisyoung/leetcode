package dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FindDuplicateFiles {
    public List<List<String>> findDuplicates(String path) throws Exception{
        List<String> allFiles = new ArrayList<>();
        dfsSearchAll(path, allFiles);
        Map<String, List<String>> map = new HashMap<>();
        for (String file : allFiles) {
            String hashcode = findFileHash(file);
            map.putIfAbsent(hashcode, new ArrayList<>());
            map.get(hashcode).add(file);
        }
        List<List<String>> result = new ArrayList<>();
        for (String key : map.keySet()) {
            if (map.get(key).size() > 1) {
                result.add(map.get(key));
            }
        }
        return result;
    }

    public List<List<String>> findDuplicatesOpt(String path) throws Exception{
        List<List<String>> result = new ArrayList<>();
        if(path==null || path.length()==0) return result;

        Map<Long, List<String>> fileSizeMap = new HashMap<>();
        getAllFilesBySize(path, fileSizeMap);
        Map<String, List<String>> dupFilesMap = new HashMap<>();

        for(List<String> filePaths: fileSizeMap.values()) {
            if(filePaths.size() < 2) continue;
            for(String filePath: filePaths) {
                String hashCode = findFileHash(filePath);
                if (!dupFilesMap.containsKey(hashCode)) {
                    dupFilesMap.put(hashCode, new ArrayList<>());
                }
                dupFilesMap.get(hashCode).add(filePath);
            }
        }

        for(List<String> dupFiles: dupFilesMap.values()) {
            if(dupFiles.size()>1)
                result.add(dupFiles);
        }

        return result;
    }

    private void getAllFilesBySize(String path, Map<Long, List<String>> fileSizeMap) {
        File f = new File(path);
        if (f.isFile()) {
            Long size = f.length();
            fileSizeMap.putIfAbsent(size, new ArrayList<>());
            fileSizeMap.get(size).add(path);
            return;
        }
        if (f.isDirectory()) {
            for (String next : f.list()) {
                getAllFilesBySize(path + "/" + next, fileSizeMap);
            }
        }
    }

    private String findFileHash(String file) throws Exception {
        File f = new File(file);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        try {
            FileInputStream inputStream = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, bytesRead);
            }
            byte[] hashedBytes = md5.digest();
            return convertByteArrayToHexString(hashedBytes);
        }
        catch (IOException ex) {
            throw new Exception("Could not generate hash from file", ex);
        }
    }

    private String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

    private void dfsSearchAll(String path, List<String> allFiles) {
        File f = new File(path);
        if (f.isFile()) {
            allFiles.add(path);
            return;
        }
        if (f.isDirectory()) {
            for (String next : f.list()) {
                dfsSearchAll(path + "/" + next, allFiles);
            }
        }
    }
}
