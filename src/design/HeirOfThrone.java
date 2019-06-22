package design;

import java.util.*;

public class HeirOfThrone {
    Map<String, List<String>> tree = new HashMap<>();
    Set<String> dead = new HashSet<>();
    String root = "king";

    public HeirOfThrone(){
        tree.put("king", new ArrayList<>());
    }

    public void birth(String parent, String name) {
        if(!tree.containsKey(parent)) {
            // throw exception
        } else {
            tree.get(parent).add(name);
            tree.put(name, new ArrayList<>());
        }
    }

    public void death(String name) {
        dead.add(name);
    }
//    Zelong Qiu: 这个方程只是lazy delete会不会导致dead越存越大？面试官会不会问如果需要实际删除发方法
//    根据看到的面经，还没有人被遇到过这个问题，如果有人遇到需要实际删除的问题，欢迎提供思路和代码
//
//    Provider: Yang Qi
//    对于动态删，提供一个思路，用一个map记录子节点到父节点的映射，如果当前节点挂掉，把自己的子节点插入的父节点自身对应的位置之后

    public List<String> getOrder(){
        List<String> res = new ArrayList<>();
        dfs(root, res);
        return res;
    }

    private void dfs(String curr, List<String> res) {
        if(!dead.contains(curr)) {
            res.add(curr);
        }
        for(String child : tree.get(curr)) {
            dfs(child, res);
        }
    }
}
