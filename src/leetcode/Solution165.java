package leetcode;

import java.util.Arrays;

public class Solution165 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Arrays.toString("11.36".split("\\.")));
	}
    public int compareVersion(String version1, String version2) {
        // 按照.进行分割
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        int i = 0;
        // 比对相应的子串
        for(; i < v1.length && i < v2.length; i++){
            int val1 = Integer.parseInt(v1[i]);
            int val2 = Integer.parseInt(v2[i]);
            if(val1 < val2) return -1;
            if(val1 > val2) return 1;
        }
        // 如果某个版本号更长，判断其多余部分是否是0，如果不是0，则较长的较大，否则是一样的。
        if(v2.length > v1.length){
            for(; i < v2.length; i++){
                int val = Integer.parseInt(v2[i]);
                if(val != 0) return -1;
            }
        } else if(v1.length > v2.length){
            for(; i < v1.length; i++){
                int val = Integer.parseInt(v1[i]);
                if(val != 0) return 1;
            }
        }
        return 0;
    }
}
