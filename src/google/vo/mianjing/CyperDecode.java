package google.vo.mianjing;

import java.util.ArrayList;
import java.util.List;

public class CyperDecode {
    /*
    coding 2：密码锁破解。一共五个数字，每次可以按一个或多个数字，穷举所有的密码输入。
    例如"1-2-3-45"表示第一下按1，第二下按2，第三下按3，第四下45同时按，同时按的几个数字不考虑先后顺序，
    搜索+二进制状压。follow up估计是用二进制优化搜索中的状态。但因为一开始就用了二进制所以这轮20分钟就结束了。
     */

    public static List<List<String>> cypherDecode(int n) {
        List<List<String>> result = new ArrayList<>();

        dfsSearch(1, n, result, new ArrayList<>());
        return result;
    }

    private static void dfsSearch(int cur, int n, List<List<String>> result, ArrayList<String> list) {

        if (cur > n) {
            result.add(new ArrayList<>(list));
            return;
        }
        String str = "";
        for (int i = cur; i <= n; i++) {
            if (i == cur) {
                str = Integer.toString(i);
            } else {
                str += "-" + i;
            }
            list.add(str);
            dfsSearch(i + 1, n, result, list);
            list.remove(list.size() - 1);
        }
    }

    public static void main(String[] args) {
        System.out.println(cypherDecode(5));
    }
}
