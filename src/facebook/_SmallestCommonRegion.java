package facebook;

import java.util.*;

public class _SmallestCommonRegion {
    public String findSmallestRegion2(List<List<String>> regions, String region1, String region2) {
        Map<String, String> parent = new HashMap();
        for(List<String> list: regions){
            for(int i=1; i<list.size(); i++){
                parent.put(list.get(i), list.get(0));
            }
        }
        Set<String> set = new HashSet();

        while(region1!=null){ //add complete hierarchy of either region1 or region2, incl the region itself
            set.add(region1);
            region1 = parent.get(region1);
        }
        while(!set.contains(region2)){ //keep checking for other region's hierarchy in previous hierarchy, from lowest i.e. the region itself.
            region2 = parent.get(region2);
        }
        return region2;
    }
    class TreeNode {
        List<TreeNode> children;
        String name;

        public TreeNode(String name) {
            this.name = name;
            children = new ArrayList<>();
        }
    }

    TreeNode root;
    Map<String, TreeNode> map = new HashMap<>();

    private void buildTree(List<List<String>> regions) {
        for (List<String> list: regions) {
            TreeNode node = map.get(list.get(0));
            if (node == null) {
                node = new TreeNode(list.get(0));
                map.put(list.get(0), node);
            }
            if (root == null) {
                root = node;
            }
            for (int i = 1; i < list.size(); i++) {
                TreeNode child = map.get(list.get(i));
                if (child == null) {
                    child = new TreeNode(list.get(i));
                    map.put(list.get(i), child);
                }
                node.children.add(child);
            }
        }
    }

    private TreeNode getRegion(TreeNode node, TreeNode region1, TreeNode region2) {
        if (node == null) {
            return null;
        }
        if (node == region1 || node == region2) {
            return node;
        }
        int counter = 0;
        TreeNode found = null;
        for (TreeNode child: node.children) {
            TreeNode subRegion = getRegion(child, region1, region2);
            if (subRegion != null) {
                counter++;
                found = subRegion;
            }
        }
        if (counter == 2) {
            return node;
        }
        return found;
    }

    public String findSmallestRegion(List<List<String>> regions, String region1, String region2) {
        if (regions == null || regions.size() == 0) {
            return "";
        }
        buildTree(regions);
        TreeNode parent = getRegion(root, map.get(region1),  map.get(region2));
        return parent.name;
    }
}
