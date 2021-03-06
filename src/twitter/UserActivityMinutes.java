package twitter;

import java.text.SimpleDateFormat;
import java.util.*;

public class UserActivityMinutes {
//    第一道：给一堆用户以及其活跃时间的tuple list [<userID1, activeTime1>.....<userIDn,activeTimen>], 其中用户可以活跃于多个
//    timestamp，在同一分钟内（e.g. 10：01：01 and 10：01：59）活跃多次的只记为一个active minute；并且activeTime格式整数，
//    记录了从1975.1.1到当时的毫秒数。问题是让统计不同cumulated number of active minutes 对应的用户数量
//    （e.g. 总共活跃了x分钟的用户有多少个）解法：用hashmap和set解了一波，写完程序还要运行一下。面试官follow up，问这个东西用
//    mapreduce怎么解决。楼主有五六年没用过map reduce，凭印象讲了一下MapReduce的原理和怎么apply在这个问题上，面试官说差不多答对70%，
//    不过念在我不咋用的份上就不深究了。。。
//    Example:
//    Raw logs
//    {1, 1518290973}
//    {2, 1518291032}
//    {3, 1518291095}
//    {1, 1518291096}
//    {4, 1518291120}
//    {3, 1518291178}
//    {1, 1518291200}
//    {1, 1518291200}
//
//    Interval size
//      2
//
//    Resulting histogram
//    {2, 2}
//
//      2 users spend 0 - 1 minutes on Twitter
//      2 users spend 2 - 3 minutes on Twitter
    public static int[] uam(int[][] logs) {
        long start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
        Map<Integer, List<Long>> map = new HashMap<>();
        for (int[] log : logs) {
            int id = log[0];
            long time = log[1];
            if (!map.containsKey(id)) {
                map.put(id, new ArrayList<>());
            }
            map.get(id).add(time);
            start = Math.min(time, start);
            end = Math.max(end, time);
        }
        int n = findTimeDiff(start, end);
        int[] bucket = new int[n];
        System.out.println(n);
        for (int id : map.keySet()) {
            if (map.get(id).size() == 1) {
                bucket[0]++;
            } else {
                int mins = countLength(map.get(id));
                bucket[mins]++;
            }
        }
        return bucket;
    }

    private static int findTimeDiff(long start, long end) {
        Date d1 = new Date(Long.parseLong(Long.toString(start)));
        Date d2 = new Date(Long.parseLong(Long.toString(end)));
        System.out.println("d1 " + d1.toString() + " start " + start);
        System.out.println("d2 " + d2.toString() + " end " + end);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(start);
        calendar2.setTimeInMillis(end);
        int diff = calendar2.get(Calendar.SECOND) - calendar1.get(Calendar.SECOND);
        System.out.println("diffSeconds " + diff);
        return diff;
    }

    private static int countLength(List<Long> list) {
        Date start = new Date(Long.parseLong(Long.toString(list.get(0))));
        Date end = new Date(Long.parseLong(Long.toString(list.get(list.size() - 1))));
        long diff = end.getTime() - start.getTime();
        long diffSeconds = diff / 1000 % 60;
        return (int) diffSeconds;
    }

    public static void main(String[] args) {
        int[][] logs = {{1, 1518290973},
                {2, 1518291032},
                {3, 1518291095},
                {1, 1518291096},
                {4, 1518291120},
                {3, 1518291178},
                {1, 1518291200},
                {1, 1518291200}};
        System.out.println(Arrays.toString(uam(logs)));
        Date d1 = new Date(1518290973), d2 = new Date(1518291200);
        System.out.println(d1.toString());
        System.out.println(d2.toString());
        System.out.println(d2.getTime() - d1.getTime());
    }

}
