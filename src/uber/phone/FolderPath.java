package uber.phone;

import java.util.*;

public class FolderPath {
    static class Folder {
        int id;
        String name;
        List<Integer> subs;
        Folder parent;
        public Folder(int id, String name, List<Integer> subs) {
            this.id = id;
            this.name = name;
            this.subs = subs;
        }

        @Override
        public String toString() {
            return "Folder{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Folder)) return false;
            Folder folder = (Folder) o;
            return id == folder.id && Objects.equals(name, folder.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    public static List<String> findPath(List<Folder> lists, int dest) {
        Map<Integer, Folder> map = new HashMap<>();
        List<Folder> roots = new ArrayList<>();
        for (Folder folder : lists) {
            if (folder.id == 0) {
                roots.add(folder);
            } else {
                map.put(folder.id, folder);
            }
        }
        System.out.println(map);
        for (Folder root : roots) {
            List<String> result = bfsFindFolder(root, map, dest);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    private static List<String> bfsFindFolder(Folder root, Map<Integer, Folder> map, int dest) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(root.id);

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            Folder curFolder = cur == 0 ? root : map.get(cur);
            if (cur == dest) {
                return buildPath(curFolder);
            }
            for (int next : curFolder.subs) {
                if (!map.containsKey(next)) {
                    continue;
                }
                queue.add(next);
                Folder nextFolder = map.get(next);
                nextFolder.parent = curFolder;
            }
        }
        return new ArrayList<>();
    }

    private static List<String> buildPath(Folder curFolder) {
        List<String> result = new ArrayList<>();
        while (curFolder != null) {
            result.add(0, curFolder.name);
            curFolder = curFolder.parent;
        }
        return result;
    }

    public static void main(String[] args) {
        List<Folder> list = Arrays.asList(
                new Folder(0, "abc", Arrays.asList(7,3)),
                new Folder(0, "xyz", Arrays.asList()),
                new Folder(8, "def", Arrays.asList()),
                new Folder(7, "ijk", Arrays.asList(9)),
                new Folder(9, "lmn", Arrays.asList())
        );

        System.out.println(findPath(list , 8));
    }
}
