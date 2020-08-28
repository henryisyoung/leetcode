package apple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindDuplicateFileSystem {
    public static List<List<String>> findDuplicate(String[] paths) {
        List<List<String>> result = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        for (String path : paths) {
            String[] files = path.split(" ");
            String dirc = files[0];
            for (int i = 1; i < files.length; i++) {
                int pos = files[i].indexOf("(");
                String fileName = files[i].substring(0, pos);
                String fileContext = files[i].substring(pos + 1, files[i].length() - 1);
                map.putIfAbsent(fileContext, new ArrayList<>());
                map.get(fileContext).add(dirc + "/" + fileName);
            }
        }
        for (List<String> list : map.values()) {
            if (list.size() >= 2) result.add(list);
        }
        return result;
    }

    public static void main(String[] args) {
        String[] paths = {"root/a 1.txt(abcd) 2.txt(efgh)", "root/c 3.txt(abcd)", "root/c/d 4.txt(efgh)", "root 4.txt(efgh)"};
        List<List<String>> result = findDuplicate(paths);
        System.out.println(result);
    }
}
