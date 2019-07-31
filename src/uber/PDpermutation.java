package uber;

import java.util.*;

public class PDpermutation {
    public List<List<String>> permutation(List<String> input) {
        int len = input.size();
        List<List<String>> res = new ArrayList<>();
        boolean[] visited = new boolean[len];
        dfs(input, visited, new ArrayList<>(), res);

        return res;
    }

    private void dfs(List<String> input, boolean[] visited, List<String> cur, List<List<String>> res) {
        if (cur.size() == input.size()) {
            res.add(new ArrayList<>(cur));
            return;
        }

        for (int i = 0; i < input.size(); ++i) {
            if (i % 2 == 0 && visited[i] || i % 2 == 1 && (!visited[i - 1] || visited[i]))
                continue;

            visited[i] = true;
            cur.add(input.get(i));
            dfs(input, visited, cur, res);
            cur.remove(cur.size() - 1);
            visited[i] = false;
        }
    }
}
