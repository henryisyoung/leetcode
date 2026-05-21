package netflix.ProblemSolving;
/*
Mini-jq: query a parsed JSON tree by path expression.

Input
  - root : parsed JSON object   (Java view: Map<String,Object> = JSON object,
                                 List<Object> = JSON array, String/Number/
                                 Boolean/null for primitives.)
  - path : query string like ".", ".name.email", ".*.email.*"

Output
  - List<Object>  -- the values matched by the path, in traversal order.

Path grammar
  PATH      := '.'                    | '.' TOKEN ( '.' TOKEN )*
  TOKEN     := IDENT | '*'
  IDENT     := [a-zA-Z0-9_-]+         (anything that's not '.' or '*')

Semantics
  '.'         identity         -> [root]
  '.field'    object field     -> for each node n in frontier, if n is a
                                  Map with key "field", add n.get("field").
                                  Missing fields silently drop the branch.
  '.*'        wildcard          -> for each Map node, add all values.
                                  For Lists, add every element (a useful
                                  generalization noted in the prompt as
                                  "wildcard over arrays vs objects").
                                  Primitives are skipped.

Examples (with root = {"a":{"x":1,"y":2}, "b":[10,20]})
  "."              -> [root]
  ".a"             -> [{"x":1,"y":2}]
  ".a.x"           -> [1]
  ".*"             -> [{"x":1,"y":2}, [10,20]]
  ".*.x"           -> [1]                  (only .a is a Map with "x")
  ".b.*"           -> [10, 20]             (wildcard descends into the array)
  ".missing"       -> []                   (silently empty)

Constraints / decisions
  - Fields not found are silently skipped (no exception, no nulls in result).
  - Wildcard on primitives contributes nothing.
  - Order: Map values are in entry-set order (use LinkedHashMap upstream
    for stable order); List elements are in index order.
*/

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
Algorithm: BFS-style frontier expansion.

  frontier <- [root]
  for each token in tokens:
      next <- []
      for each node in frontier:
          if token == '*':
              if node is Map: next += node.values()
              elif node is List: next += node          (decision)
              else: skip
          else:                                        (named field)
              if node is Map and node has token: next += [node.get(token)]
              else: skip
      frontier <- next
  return frontier

  Why frontier (not single-pointer DFS):
    Wildcards naturally branch the search; carrying a frontier keeps the
    code one loop deep without explicit recursion or stack.

  Why "skip silently" on misses:
    The spec asks for matching values; missing fields aren't an error,
    they just don't contribute.  This makes ".a.b.c" robust to partial
    data.

Complexity
  Let N = total nodes in the JSON tree, P = number of path tokens.
  Time:   O(P * size of largest frontier) -- worst case O(P * N) when '.*' is used.
  Memory: O(size of frontier)
*/
public class MiniJqQuery {

    /** Returns the list of nodes matched by `path` rooted at `root`. */
    public List<Object> query(Object root, String path) {
        List<Object> frontier = new ArrayList<>();
        frontier.add(root);

        if (path == null || path.isEmpty() || path.equals(".")) {
            return frontier;                       // identity
        }
        if (path.charAt(0) != '.') {
            throw new IllegalArgumentException("path must start with '.': " + path);
        }

        for (String token : tokenize(path)) {
            List<Object> next = new ArrayList<>();
            for (Object node : frontier) {
                if (token.equals("*")) {
                    if (node instanceof Map) {
                        next.addAll(((Map<?, ?>) node).values());
                    } else if (node instanceof List) {
                        next.addAll((List<?>) node);
                    }
                } else {
                    if (node instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) node;
                        if (m.containsKey(token)) next.add(m.get(token));
                    }
                }
            }
            frontier = next;
            if (frontier.isEmpty()) break;          // dead branch; no need to keep walking
        }
        return frontier;
    }

    /** Split ".a.b.*" into ["a", "b", "*"].  Rejects "..", trailing ".", and empty tokens. */
    private static List<String> tokenize(String path) {
        String[] parts = path.substring(1).split("\\.", -1);  // keep trailing empties
        List<String> out = new ArrayList<>(parts.length);
        for (String tok : parts) {
            if (tok.isEmpty()) throw new IllegalArgumentException("empty token in path: " + path);
            out.add(tok);
        }
        return out;
    }

    /* --------------------------- demo / tests --------------------------- */

    public static void main(String[] args) {
        MiniJqQuery jq = new MiniJqQuery();

        // Build a sample JSON tree:
        //   {
        //     "users": [
        //       {"name": "alice", "email": "a@x"},
        //       {"name": "bob",   "email": "b@x"}
        //     ],
        //     "team": {
        //       "lead":  {"email": "lead@x"},
        //       "intern":{"email": "intern@x"}
        //     },
        //     "version": 3
        //   }
        Map<String, Object> root = new LinkedHashMap<>();
        List<Object> users = new ArrayList<>();
        users.add(map("name", "alice", "email", "a@x"));
        users.add(map("name", "bob",   "email", "b@x"));
        root.put("users", users);

        Map<String, Object> team = new LinkedHashMap<>();
        team.put("lead",   map("email", "lead@x"));
        team.put("intern", map("email", "intern@x"));
        root.put("team", team);

        root.put("version", 3);

        // ---- identity ----
        check(jq, root, ".",                  list(root));
        check(jq, root, "",                   list(root));     // tolerated
        check(jq, root, null,                 list(root));     // tolerated

        // ---- single field ----
        check(jq, root, ".version",           list(3));
        check(jq, root, ".missing",           list());

        // ---- nested field ----
        check(jq, root, ".team.lead.email",   list("lead@x"));
        check(jq, root, ".team.lead.missing", list());

        // ---- wildcard over object ----
        check(jq, root, ".team.*.email",      list("lead@x", "intern@x"));

        // ---- wildcard over array ----
        check(jq, root, ".users.*.email",     list("a@x", "b@x"));

        // ---- wildcard at the end ----
        check(jq, root, ".team.lead.*",       list("lead@x"));

        // ---- chained wildcards ".*.*.email" hits team's leaf objects ----
        // root.* -> [users(list), team(map), 3]
        // *      -> users elements + team values + (3 is primitive, skipped)
        //       -> [{name,email}, {name,email}, {email}, {email}]
        // .email -> ["a@x", "b@x", "lead@x", "intern@x"]
        check(jq, root, ".*.*.email",
                list("a@x", "b@x", "lead@x", "intern@x"));

        // ---- wildcard on primitive contributes nothing ----
        check(jq, 42, ".*", list());
        check(jq, "hello", ".x", list());

        // ---- empty array / empty object ----
        check(jq, list(), ".*",     list());
        check(jq, new LinkedHashMap<>(), ".*", list());

        // ---- malformed path ----
        try {
            jq.query(root, "..bad");
            System.out.println("FAIL  '..bad' should throw");
        } catch (IllegalArgumentException ok) {
            System.out.println("OK    '..bad' rejected");
        }
        try {
            jq.query(root, "no-leading-dot");
            System.out.println("FAIL  'no-leading-dot' should throw");
        } catch (IllegalArgumentException ok) {
            System.out.println("OK    'no-leading-dot' rejected");
        }
    }

    @SafeVarargs
    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    private static List<Object> list(Object... vs) {
        List<Object> l = new ArrayList<>();
        for (Object v : vs) l.add(v);
        return l;
    }

    private static void check(MiniJqQuery jq, Object root, String path, List<Object> expected) {
        List<Object> got = jq.query(root, path);
        boolean ok = got.equals(expected);
        System.out.println((ok ? "OK    " : "FAIL  ") + "query " + path
                + " expected=" + expected + " got=" + got);
    }
}
