package dropbox;

import java.util.*;
//https://www.1point3acres.com/bbs/thread-341713-2-1.html
public class FileAccess {
// /A
// |___ /B
//     |___ /C <-- access
//     |___ /D
// |___/E <-- access
//     |___ /F
//         |___ /G


// folders = [
//   ('A', None),
//   ('B', 'A'),
//   ('C', 'B'),
//   ('D', 'B'),
//   ('E', 'A'),
//   ('F', 'E'),
// ]

// access = set(['C', 'E'])

// has_access(String folder_name) -> boolean. 1point3acres.com/bbs

// has_access("B") -> false
// has_access("C") -> true
// has_access("F") -> true
// has_access("G") -> true
    Set<String> access, noAccess;
    String[][] folders;
    Map<String, String> map;
    public FileAccess(Set<String> access, String[][] folders) {
        this.access = access;
        this.noAccess = new HashSet<>();
        this.folders = folders;
        this.map = new HashMap<>();
        initMap(map, folders);
    }

    private void initMap(Map<String, String> map, String[][] folders) {
        for (String[] folder : folders) {
            String father = folder[1], child = folder[0];
            map.put(child, father);
        }
    }

    public boolean hasAccess(String file) {
        if (access.contains(file)) return true;
        if (noAccess.contains(file)) return false;
        if (map.get(file) == null) {
            noAccess.add(file);
            return false;
        }
        if (map.containsKey(file)) {
            if (hasAccess(map.get(file))) {
                access.add(file);
                return true;
            }
        }
        noAccess.add(file);
        return false;
    }

    public static void main(String[] args) {
        String[][] folders = {{"A", null}, {"B", "A"}, {"C", "B"}, {"D", "B"},{"E", "A"},{"F", "E"}, {"G", "F"}};
        Set<String> access = new HashSet<>(Arrays.asList("C", "E"));
        FileAccess solver = new FileAccess(access, folders);
        System.out.println(solver.hasAccess("B"));
        System.out.println(solver.hasAccess("A"));
        System.out.println(solver.hasAccess("C"));
        System.out.println(solver.hasAccess("F"));
        System.out.println(solver.hasAccess("G"));
        System.out.println(solver.access.toString());
        System.out.println(solver.noAccess.toString());
    }
}
