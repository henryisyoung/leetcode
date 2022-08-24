package uber.phone;

import java.util.*;

public class FolderPath2 {
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

    public static List<String> findPath(List<Folder> folders, int dest) {
        Map<Integer, Folder> map = new HashMap<>();
        List<Folder> roots = new ArrayList<>();
        for (Folder folder : folders) {
            if (folder.id == 0) {
                roots.add(folder);
            } else {
                map.put(folder.id, folder);
            }
        }
        System.out.println(map);
        for (Folder root : roots) {
            List<String> result = new ArrayList<>();
            if (findPathHelper(root, result, map, dest)) {
                return result;
            }
        }
        return new ArrayList<>();
    }

    private static boolean findPathHelper(Folder folder, List<String> result, Map<Integer, Folder> map, int dest) {
        if (folder.id == dest) {
            result.add(folder.name);
            return true;
        }
        if (folder.subs.isEmpty()) {
            return false;
        }
        result.add(folder.name);
        for(int next : folder.subs) {
            if (map.containsKey(next)) {
                if (findPathHelper(map.get(next) , result, map, dest)) {
                    return true;
                }
            }
        }
        result.remove(result.size() - 1);
        return false;
    }


    public static void main(String[] args) {
        List<Folder> list = Arrays.asList(
                new Folder(0, "abc", Arrays.asList(7,3)),
                new Folder(0, "xyz", Arrays.asList()),
                new Folder(8, "def", Arrays.asList()),
                new Folder(7, "ijk", Arrays.asList(9)),
                new Folder(9, "lmn", Arrays.asList(10)),
                new Folder(10, "l0n", Arrays.asList())
        );

        System.out.println(findPath(list , 10));
    }
}
