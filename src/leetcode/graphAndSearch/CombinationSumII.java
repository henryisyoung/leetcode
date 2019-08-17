package leetcode.graphAndSearch;

import java.util.ArrayList;
import java.util.List;

public class CombinationSumII {
    public static List<List<Integer>> combinationSumDP(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        int[] counts = new int[target+1];
        for(int k:candidates){
            if(k<=target){
                counts[k]++;
            }
        }
        List<Integer> valiable = new ArrayList<>();
        for(int i=1;i<=target;i++){
            if(counts[i]!=0){
                valiable.add(i);
            }
        }
        if(valiable.size()==0){
            return new ArrayList<>();
        }
        List<List<Integer>>[] mainList=new List[target+1];
        List<Integer> tmp=new ArrayList();
        result.add(tmp);
        mainList[0]=result;
        for(int i=valiable.get(0);i<=target;i++){
            List<List<Integer>> tmpResult=new ArrayList<>();
            for(int k:valiable){
                if(k<=i){
                    if(k==i||k<=i/2){
                        List<List<Integer>> list=mainList[i-k];
                        for (List<Integer> l:list){
                            if (l.size()==0||l.get(0)>=k){
                                int count=counts[k];
                                for(int num:l){
                                    if(num==k){
                                        count--;
                                    }
                                }
                                if(count!=0){
                                    tmpResult.add(generateList(l,k));
                                }
                            }
                        }
                    }
                }else{
                    break;
                }
            }
            mainList[i]=tmpResult;
        }
        return mainList[target];
    }

    private static List<Integer> generateList(List<Integer> l,int k){
        List<Integer> list=new ArrayList<>();
        list.add(k);
        for (int m:l){
            list.add(m);
        }
        return list;
    }

    public static void main(String[] args) {
        int[] can = {10,1,2,7,6,1,5};
        int target = 8;
        System.out.println(combinationSumDP(can, target));
    }
}
