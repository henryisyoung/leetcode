package leetcode.graphAndSearch.bipartiteGraphAndMatching;

import java.util.Arrays;

// KM Algorithm https://blog.csdn.net/u011721440/article/details/38169201
public class HouseAssignment {
    int[] link, lx, ly,  slack;
    boolean[] visx, visy;
    int[][] graph;
    int inf = Integer.MAX_VALUE;
    int n;

    public int assignHouse(int N, int[][] nums) {
        n = N;
        link = new int[n];
        graph = nums;
        Arrays.fill(link, -1);
        lx = new int[n];
        ly = new int[n];
        int i, j;
        for(i = 0; i < n; i++) {     //让每个村民都尽量买最贵的房子
            for(j = 0; j < n; j++) {
                lx[i] = Math.max(lx[i], graph[i][j]);
            }
        }
        for(i = 0; i < n; i++) {
            for(j = 0; j < n; j++) {
                slack[j] = inf;
            }
            while(true) {
                visx = new boolean[n];
                visy = new boolean[n];
                if(find(i)) { //当村民都找到房子时，跳出循环
                    break;
                } else {
                    int d = inf;
                    for(j = 0; j < n; j++) {    //匹配失败，在上轮没有选中的房子里，
                        if(!visy[j]) {   //找到稍微便宜一点的房子，作为备选房子
                            d = Math.min(d, slack[j]); //d是一个备选房价和当前房价最小差值
                        }
                    }
                    for(j = 0; j < n; j++) {
                        if(visx[j]) {
                            lx[j] -= d;
                        }
                        if(visy[j]) {
                            ly[j] += d;
                        }
                    }
                }
            }
        }
        int ans=0;
        for(i=0;i<n;i++) {
            ans += graph[link[i]][i];
        }
        return ans;
    }

    private boolean find(int k) {
        visx[k]=true;
        int i, d;
        for(i = 0; i < n; i++) {
            if(visy[i]) {
                continue;
            }
            d = lx[k] + ly[i] - graph[k][i];
            if(d == 0) {
                visy[i]=true;
                if(link[i] == -1 || find(link[i])) {
                    link[i]=k;  //第K个村民选择第I个房子
                    return true;
                }
            }
            else { //d!=0
                slack[i]=Math.min(slack[i], d);
            }
        }
        return false;
    }

    public static void main(String[] args) {
    }
}

