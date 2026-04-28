package snowflake;

import java.util.*;

public class FileSystemTrie {
    private static class Node {
        private final String name;
        private final Map<String, Node> nodeByName = new TreeMap<>();
        private final StringBuilder content = new StringBuilder();
        private boolean isFile;

        Node(final String name) {
            this.name = name;
        }

        private boolean isFile() {
            return isFile;
        }
    }

    private final Node root = new Node("");

    public FileSystemTrie() {
    }

    /**
     * @param path file or directory path
     * @return If path is a file path, returns a list that only contains this file's name.
     * If path is a directory path, returns the list of file and directory names in this directory.
     */
    public List<String> ls(final String path) {
        var node = buildPath(path);
        return node.isFile() ? List.of(node.name) : List.copyOf(node.nodeByName.keySet());
    }

    /**
     * Makes a new directory according to the given path. The given directory path does not exist.
     * If the middle directories in the path do not exist, you should create them as well.
     *
     * @param path directory path
     */
    public void mkdir(final String path) {
        buildPath(path);
    }

    /**
     * If filePath does not exist, creates that file containing given content.
     * If filePath already exists, appends the given content to original content.
     *
     * @param filePath filepath
     * @param content  content of the file
     */
    public void addContentToFile(final String filePath, final String content) {
        var node = buildPath(filePath);
        node.isFile = true;
        node.content.append(content);
    }

    /**
     * @param filePath file path
     * @return the content in the file at filePath.
     */
    public String readContentFromFile(final String filePath) {
        return buildPath(filePath).content.toString();
    }

    private Node buildPath(String path) {
        Node current = root;

        // split path like "/a/b/c" -> ["", "a", "b", "c"]
        String[] parts = path.split("/");

        // start from index 1 to skip the empty string before the first "/"
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            // get existing child node, or create one if it doesn't exist
            current = current.nodeByName.computeIfAbsent(
                    part,
                    name -> new Node(name)
            );
        }

        return current;
    }
}
