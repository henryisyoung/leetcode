package twitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
//                System.out.println("file " + file + " file name " + fileName + " fileContent " + fileContent);
                if(!map.containsKey(fileContent)) {
                    map.put(fileContent, new ArrayList<>());
                }
                String filePath = arr[0] + "/" +fileName;
//                System.out.println("filePath " + filePath);
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
