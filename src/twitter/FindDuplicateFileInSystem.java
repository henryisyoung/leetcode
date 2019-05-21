package twitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Follow up questions:

 1. Imagine you are given a real file system, how will you search files? DFS or BFS ?
 In general, BFS will use more memory then DFS. However BFS can take advantage of the locality of files in inside directories,
 and therefore will probably be faster

 2. If the file content is very large (GB level), how will you modify your solution?
 In a real life solution we will not hash the entire file content, since it's not practical. Instead we will first map all
 the files according to size. Files with different sizes are guaranteed to be different.
 We will than hash a small part of the files with equal sizes (using MD5 for example). Only if the md5 is the same,
 we will compare the files byte by byte

 3. If you can only read the file by 1kb each time, how will you modify your solution?
 This won't change the solution. We can create the hash from the 1kb chunks, and then read the entire file if a full byte
 by byte comparison is required.

 What is the time complexity of your modified solution? What is the most time consuming part and memory consuming part of it?
 How to optimize?
 Time complexity is O(n^2 * k) since in worse case we might need to compare every file to all others. k is the file size

 How to make sure the duplicated files you find are not false positive?
 We will use several filters to compare: File size, Hash and byte by byte comparisons.
 */
public class FindDuplicateFileInSystem {
    //https://leetcode.com/problems/find-duplicate-file-in-system/discuss/104123/C%2B%2B-clean-solution-answers-to-follow-up
    public List<List<String>> findDuplicate(String[] paths) {
        List<List<String>> result = new ArrayList<>();
        if (paths == null || paths.length == 0) {
            return result;
        }
        Map<String, List<String>> map = new HashMap<>();
        for (String path : paths) {
            String[] arr = path.split(" ");
            for (int i = 1; i < arr.length; i++) {
                String file = arr[i];
                String fileName = file.substring(0, file.indexOf("("));
                String fileContent = file.substring(file.indexOf("(") + 1, file.indexOf(")"));
                if(!map.containsKey(fileContent)) {
                    map.put(fileContent, new ArrayList<>());
                }
                String filePath = arr[0] + "/" +fileName;
                map.get(fileContent).add(filePath);
            }
        }
        for (String content : map.keySet()) {
            if (map.get(content).size() >1) {
                result.add(map.get(content));
            }
        }
        return result;
    }

    public boolean isSameFile(File f1, File f2) throws IOException {
        byte[] file1 = Files.readAllBytes(f1.toPath());
        byte[] file2 = Files.readAllBytes(f2.toPath());
        return Arrays.equals(file1, file2);
    }

    public static void main(String[] args) {
        String[] paths = {"root/a 1.txt(abcd) 2.txt(efgh)", "root/c 3.txt(abcd)",
                "root/c/d 4.txt(efgh)", "root 4.txt(efgh)"};
        FindDuplicateFileInSystem solver = new FindDuplicateFileInSystem();
        System.out.println(solver.findDuplicate(paths).toString());
    }
}
