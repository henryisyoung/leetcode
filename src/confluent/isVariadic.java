package confluent;

import java.util.*;

public class isVariadic {
    static class TrieNode {
        List<String> isVar, notVar;
        Map<String,TrieNode> children;
        public TrieNode() {
            this.children = new HashMap<>();
            this.isVar = new ArrayList<>();
            this.notVar = new ArrayList<>();
        }
    }

    static class Trie {
        TrieNode root;
        public Trie() {
            this.root = new TrieNode();
        }

        public void insert(List<String> args, boolean isVar, String fun) {
            TrieNode node = root;
            for (String str : args) {
                if (!node.children.containsKey(str)) {
                    node.children.put(str, new TrieNode());
                }
                node = node.children.get(str);
            }
            if (isVar) {
                node.isVar.add(fun);
            } else {
                node.notVar.add(fun);
            }
        }
    }
    static class Func {
        String fun;
        List<String> args;
        boolean isV;
        public Func(String fun, List args, boolean isV) {
            this.args = args;
            this.fun = fun;
            this.isV = isV;
        }
    }
    public static List<String> match(List<Func> funcs, List<String> args) {
        Trie trie = new Trie();
        for (Func func : funcs) {
            trie.insert(func.args, func.isV, func.fun);
        }
        List<String> result = new ArrayList<>();
        TrieNode node = trie.root;
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (!node.children.containsKey(arg)) {
                return result;
            }
            TrieNode next = node.children.get(arg);
            if (i != args.size() - 1 && !next.isVar.isEmpty()) {
                if(allSame(args, i)) {
                    result.addAll(next.isVar);
                }
            } else if (i == args.size() - 1) {
                result.addAll(next.isVar);
                result.addAll(next.notVar);
            }
            node = next;
        }
        return result;
    }

    private static boolean allSame(List<String> args, int i) {
        String cur = args.get(i);
        while (i < args.size()) {
            if (!args.get(i).equals(cur)) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static void main(String[] args) {
        List<Func> funcs = Arrays.asList(
                new Func("FuncA", Arrays.asList("String", "Integer", "Integer"), false),
                new Func("FuncB", Arrays.asList("String", "Integer"), true),
                new Func("FuncC", Arrays.asList("Integer"), true),
                new Func("FuncD", Arrays.asList( "Integer", "Integer"), true),
                new Func("FuncE", Arrays.asList("Integer", "Integer", "Integer"), false),
                new Func("FuncF", Arrays.asList("String"), false),
                new Func("FuncG", Arrays.asList("Integer"), false)
        );

//        System.out.println(match(funcs, Arrays.asList("String")));
//        System.out.println(match(funcs, Arrays.asList("Integer")));
        System.out.println(match(funcs, Arrays.asList("String",  "Integer", "Integer")));
        Set<List<String>> set = new HashSet<>();
        set.add(Arrays.asList("a"));
        set.add(Arrays.asList("a"));
        String s1 = Arrays.asList("1", "2").toString();
        String s2 = Arrays.asList("1").toString();
        System.out.println(s1.startsWith(s2));
        System.out.println(set.size());
    }
}
