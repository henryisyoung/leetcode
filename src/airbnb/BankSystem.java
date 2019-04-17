//package airbnb;
//
//import java.util.*;
//
///**
// * 设计一个银行帐户系统，实现:
// * 存钱(帐户id，存钱数目，日期)
// * 取钱(帐户id，存钱数目，日期)
// * 查账(帐户id，起始日期，结束日期): 只需要返回两个数值，一个是起始日期的balance，一个是结束日期
// 的balance。
// * 􏰁述就是这么多，剩下的自己发挥。钱的类型用integer，日期什么的自定义，我直接拿了integer
// * */
//public class BankSystem {
//    Map<Integer, Integer> balanceMap;
//    Map<Integer, Map<Long, Integer>> statementMap;
//    public BankSystem() {
//        this.balanceMap = new HashMap<>();
//        this.statementMap = new HashMap<>();
//    }
//
//    public void deposit(int id, int val, long timeStamp) {
//        if (!balanceMap.containsKey(id)) {
//            balanceMap.put(id, 0);
//            statementMap.put(id, new HashMap<>());
//        }
//        balanceMap.put(id, balanceMap.get(id) + val);
//        statementMap.get(id).put(timeStamp, balanceMap.get(id));
//    }
//
//    public boolean withdraw(int id, int val, long timeStamp) {
//        if (!balanceMap.containsKey(id) || balanceMap.get(id) < val) {
//            return false;
//        }
//        balanceMap.put(id, balanceMap.get(id) - val);
//        statementMap.get(id).put(timeStamp, balanceMap.get(id));
//        return true;
//    }
//
//    public int[] checkAccount(int id, long start, long end) {
//        if (!balanceMap.containsKey(id)) {
//            return new int[0];
//        }
//        int[] result = new int[2];
//        Map<Long, Integer> statements = statementMap.get(id);
//        List<Long> timeStamps = new ArrayList<>(statements.keySet());
//
//        Collections.sort(timeStamps);
//        if (timeStamps.contains(start)) {
//            result[0] = statements.get(start);
//        } else {
//            int index = searchIndex(timeStamps, start);
//            result[0] = statements.get(timeStamps.get(index));
//        }
//
//        if (timeStamps.contains(end)) {
//            result[1] = statements.get(end);
//        } else {
//            int index = searchIndex(timeStamps, end);
//            result[1] = statements.get(timeStamps.get(index));
//        }
//        return result;
//    }
//
//    private int searchIndex(List<Long> timeStamps, long start) {
//        int left = 0, right = timeStamps.size() - 1;
//        while (left + 1 < right) {
//            int mid = left + (right - left)/2;
//            if (timeStamps.get(mid) < start) {
//                left = mid;
//            } else {
//                right = mid;
//            }
//        }
//        if (timeStamps.get(right) < start) {
//            return right;
//        }
//        return left;
//    }
//
//    public static void main(String[] args) {
//        List<Long> list = Arrays.asList(11l,14l,15l,17l,18l,19l);
//        BankSystem solver = new BankSystem();
//        long start = 16l;
//        int pos = solver.searchIndex(list, start);
//        int index = -(Collections.binarySearch(list, start) + 1);
//        System.out.println(pos);
//        System.out.println(index);
//    }
//}
