package dropbox;

import java.io.*;
import java.security.*;
import java.util.*;

public class FindDuplicateFiles {
    public List<List<String>> findDuplicates(String path) throws Exception{
        List<List<String>> result = new ArrayList<>();
        if(path==null || path.length()==0) return result;

        List<String> filesPath = getAllFiles(path);
        Map<String, List<String>> dupFilesMap = new HashMap<>();

        for(String filePath: filesPath) {
            File file = new File(filePath);
            String hashCode = hashFile(file, "MD5");

            if(!dupFilesMap.containsKey(hashCode)) {
                dupFilesMap.put(hashCode, new ArrayList<>());
            }
            dupFilesMap.get(hashCode).add(filePath);
        }

        for(List<String> dupFiles: dupFilesMap.values()) {
            if(dupFiles.size()>1)
                result.add(dupFiles);
        }

        return result;
    }

    private List<String> getAllFiles(String path) {
        List<String> filesPath = new ArrayList<>();
        Stack<String> s = new Stack<>();
        s.push(path); //DFS with Stack

        while(!s.isEmpty()) {
            String currPath = s.pop();
            File file = new File(currPath);

            if(file.isFile()) {
                filesPath.add(currPath);
            } else if(file.isDirectory()) {
                String[] subDir = file.list();
                for(String dir:subDir) {
                    s.push(currPath+"/"+dir);
                }
            }
        }
        return filesPath;
    }

    private void getAllFilesDfs(String path, List<String> result) {
        File f = new File(path);
        if (f.isFile()) {
            result.add(path);
            return;
        }
        if (f.isDirectory()) {
            String[] paths = f.list();
            for (String next : paths) {
                getAllFilesDfs(path + "/" + next, result);
            }
        }
    }

    public List<List<String>> findDuplicatesOpt(String path) throws Exception{
        List<List<String>> result = new ArrayList<>();
        if(path==null || path.length()==0) return result;

        Map<Long, List<String>> fileSizeMap = getAllFilesBySize(path);
        Map<String, List<String>> dupFilesMap = new HashMap<>();

        for(List<String> filePaths: fileSizeMap.values()) {
            if(filePaths.size()<2) continue;
            for(String filePath: filePaths) {
                File file = new File(filePath);
                String hashCode = hashFile(file, "MD5");

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

    private Map<Long, List<String>> getAllFilesBySize(String path) {
        Map<Long, List<String>> fileSizeMap = new HashMap<>();
        Stack<String> s = new Stack<>();
        s.push(path);

        while(!s.isEmpty()) { //DFS by stack
            String currPath = s.pop();
            File file = new File(currPath);

            if(file.isFile()) {
                long size = file.length();
                if(!fileSizeMap.containsKey(size))
                    fileSizeMap.put(size, new ArrayList<>());
                fileSizeMap.get(size).add(currPath);
            } else if(file.isDirectory()) {
                String[] subDir = file.list();
                for(String dir:subDir) {
                    s.push(currPath+"/"+dir);
                }
            }
        }

        return fileSizeMap;
    }

    private static String hashFile(File file, String algorithm) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | IOException ex) {
            throw new Exception("Could not generate hash from file", ex);
        }
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) throws Exception{
        List<List<String>> dupFiles = new FindDuplicateFiles().findDuplicatesOpt("./Resources/Dropbox");
        for(List<String> dup: dupFiles) {
            System.out.println(dup.toString());
        }
        //new FindDuplicateFiles().read1KBEachTime("./Resources/Dropbox/board.txt");
    }
}
