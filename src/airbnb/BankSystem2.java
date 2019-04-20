package airbnb;

import java.util.*;

/**
 * 设计一个银行帐户系统，实现:
 * 存钱(帐户id，存钱数目，日期)
 * 取钱(帐户id，存钱数目，日期)
 * 查账(帐户id，起始日期，结束日期): 只需要返回两个数值，一个是起始日期的balance，一个是结束日期
 的balance。
 * 􏰁述就是这么多，剩下的自己发挥。钱的类型用integer，日期什么的自定义，我直接拿了integer
 * */
public class BankSystem2 {
    Map<Integer, Integer> balancesMap;
    Map<Integer, Map<Long, Integer>> statementsMap;

    public BankSystem2() {
        this.balancesMap = new HashMap<>();
        this.statementsMap = new HashMap<>();
    }

    public void deposit(int accountId, int val, long timeStamp) {
       if (!balancesMap.containsKey(accountId)) {
           balancesMap.put(accountId, 0);
           statementsMap.put(accountId, new HashMap<>());
       }
       balancesMap.put(accountId, balancesMap.get(accountId) + val);
       statementsMap.get(accountId).put(timeStamp, balancesMap.get(accountId));
    }

    public boolean withdraw(int accountId, int val, long timeStamp) {
        if (!balancesMap.containsKey(accountId) || balancesMap.get(accountId) < val) {
            return false;
        }
        balancesMap.put(accountId, balancesMap.get(accountId) - val);
        statementsMap.get(accountId).put(timeStamp, balancesMap.get(accountId));
        return true;
    }

    public int[] checkAccount(int accountId, long start, long end) {
       if (!statementsMap.containsKey(accountId)) {
           return null;
       }
       int[] result = new int[2];
       Map<Long, Integer> statement = statementsMap.get(accountId);
       List<Long> time = new ArrayList<>(statement.keySet());
       Collections.sort(time);
       if (time.contains(start)) {
           result[0] = statement.get(start);
       } else {
           result[0] = statement.get(searchStartIndex(time, start));
       }
       if (time.contains(end)) {
           result[1] = statement.get(end);
       } else {
           result[1] = statement.get(searchEndIndex(time, end));
       }
       return result;
    }

    private int searchStartIndex(List<Long> list, long t) {
        int l = 0; int r = list.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (list.get(mid) > t) {
                r = mid;
            } else {
                l = mid;
            }
        }
        if (list.get(l) > t) {
            return l;
        }
        return r;
    }

    private int searchEndIndex(List<Long> list, long t) {
        int l = 0; int r = list.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (list.get(mid) > t) {
                r = mid;
            } else {
                l = mid;
            }
        }
        if (list.get(r) < t) {
            return r;
        }
        return l;
    }


    public static void main(String[] args) {
        List<Long> list = Arrays.asList(11l,14l,15l,17l,18l,19l);
        BankSystem2 solver = new BankSystem2();
        long start = 16l;
        int pos = solver.searchStartIndex(list, start);
        int index = -(Collections.binarySearch(list, start) + 1);
        System.out.println(pos);
        System.out.println(index);
    }
}
