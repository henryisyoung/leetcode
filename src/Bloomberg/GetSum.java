package Bloomberg;
import java.util.*;
public class GetSum {
	//把1 ~999分成27组
    public void print3(){
            HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
            for(int i = 1; i <= 999; i++){
                    int sum = getSum(i);
                    ArrayList<String> arr = map.get(sum);
                    if(arr == null){                
                            arr = new ArrayList<String>();
                            map.put(sum, arr);
                    }
                 
                    String cur = i + "";
                    //统一往前加0 否则前后都加有重复.
                    while(cur.length() < 3){
                            cur = '0' + cur;
                    }
                    arr.add(cur);
            }
       
            int m = 0;
            for(int i = 100; i <= 999; i++){
                    int sum = getSum(i);
                    ArrayList<String> list = map.get(sum);
                    for(String str : list){
                            String temp = i + str;
                            m++;
                            System.out.println(m + "  " + temp);
                    }
            }
    }

    private int getSum(int k){
            int sum = 0;
            while(k > 0){
                    sum = sum + k%10;
                    k = k/10;
            }
            return sum;
    }

}
