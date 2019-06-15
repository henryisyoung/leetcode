package leetcode.graphAndSearch.bipartiteGraphAndMatching;

public class RollerCoaster {
//    Problem Description
//    RPG girls今天和大家一起去游乐场玩，终于可以坐上梦寐以求的过山车了。可是，过山车的每一排只有两个座位，而且还有条不成文的规矩，
//    就是每个女生必须找个个男生做partner和她同坐。但是，每个女孩都有各自的想法，举个例子把，Rabbit只愿意和XHD或PQK做partner，
//    Grass只愿意和linle或LL做partner，PrincessSnow愿意和水域浪子或伪酷儿做partner。考虑到经费问题，boss刘决定只让找到partner的人去坐过山车，
//    其他的人，嘿嘿，就站在下面看着吧。聪明的Acmer，你可以帮忙算算最多有多少对组合可以坐上过山车吗？
//    Input
//    输入数据的第一行是三个整数K , M , N，分别表示可能的组合数目，女生的人数，男生的人数。0<K<=1000
//    1<=N 和M<=500.接下来的K行，每行有两个数，分别表示女生Ai愿意和男生Bj做partner。最后一个0结束输入。

    //https://blog.csdn.net/sixdaycoder/article/details/47680831
    //https://blog.csdn.net/hurmishine/article/details/52743460
    int n,m;
    boolean[][] a;
    boolean[] inCrossPath;
    int[] match;
    int maxn;
    public RollerCoaster(int n, int m, boolean[][] a) {
        this.m = m;
        this.n = n;
        this.maxn = 505;
        this.a = a;
        this.inCrossPath = new boolean[maxn];
        this.match = new int[maxn];
    }

    public int findMaxPair() {
        int ans = 0;
        for (int i = 1; i <= m; i++) {
            inCrossPath = new boolean[maxn];
            if (FindPairs(i)) {
                ans++;
            }
        }
        return ans;
    }

    private boolean FindPairs(int x) {
        for(int i = 1; i <= n; i++) { //男生
            if(a[x][i] && !inCrossPath[i]) {
                inCrossPath[i] = true;
                //该男生没有被组合(未匹配点) || 该女生可以找其他人组合
                if(match[i] == 0 || FindPairs(match[i])) {
                    match[i] = x;//第i个男生和第x个女生做partner
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        boolean[][] a = {
                {false, true, false,false,false},
                {false,false,true,false,false},
                {false,false,false,true,false},
                {false,false,false,false,true}};
        RollerCoaster rc = new RollerCoaster(4, 4, a);
    }
}
