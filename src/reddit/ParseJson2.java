package reddit;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * {
 *   "date1": {
 *     "platform1": {
 *       "pageviews": 7,
 *       "uniqueviews": 2
 *     },
 *     "platform2": {
 *       "pageviews": 1,
 *       "uniqueviews": 3
 *     }
 *   },
 *   "date2": {
 *     "platform1": {
 *       "pageviews": 7,
 *       "uniqueviews": 2
 *     },
 *     "platform2": {
 *       "pageviews": 1,
 *       "uniqueviews": 3
 *     }
 *   }
 * }
 */
public class ParseJson2 {
    //https://jenkov.com/tutorials/java-json/jackson-jsonnode.html
    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String s = "{\"10-02-2020\":{\"platform1\":{\"pageviews\":12,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-03-2020\":{\"platform1\":{\"pageviews\":17,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-04-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-05-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-06-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":11,\"uniqueviews\":3}}}";
            TreeMap<String, Map<String, Map<String, Integer>>> map = objectMapper.readValue(s, TreeMap.class);
            System.out.println(map);
            System.out.println(pastDaysPageviews("10-10-2020", map));
            System.out.println("---");
            System.out.println(slideWindowMax(map, 3));
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    // past 7 days pageviews
    public static int pastDaysPageviews(String today, Map<String, Map<String,Map<String, Integer>>> map) {
        int count = 0;
        for (String date : map.keySet()) {
            if (isPastSevenDays(date, today)) {
                System.out.println(date);
                int pageViews = findPageView(map.get(date));
                count += pageViews;
            }
        }
        return count;
    }

    public static List<Integer> slideWindowMax(Map<String, Map<String,Map<String, Integer>>> map, int k) {
        int[] counts = buildDailyPagesViews(map);
        System.out.println(Arrays.toString(counts));
        Deque<Integer> deque = new LinkedList<>();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            while (!deque.isEmpty() && counts[i] >= counts[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.add(i);
        }
        result.add(counts[deque.peekFirst()]);

        for (int i = k; i < counts.length; i++) {
            int l = i - k;
            while (!deque.isEmpty() && deque.peekFirst() <= l) {
                deque.pollFirst();
            }
            while (!deque.isEmpty() && counts[i] >= counts[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.add(i);
            result.add(counts[deque.peekFirst()]);
        }
        return result;
    }

    private static int[] buildDailyPagesViews(Map<String, Map<String, Map<String, Integer>>> map) {
        int[] result = new int[map.size()];
        int i = 0;
        for (String date : map.keySet()) {
            result[i++] = findPageView(map.get(date));
        }
        return result;
    }

    private static int findPageView(Map<String, Map<String, Integer>> map) {
        int count = 0;
        for (Map<String, Integer> platform : map.values()) {
            count += platform.getOrDefault("pageviews", 0);
        }
        return count;
    }

    private static boolean isPastSevenDays(String date2, String date1) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            if (d1.getTime() < d2.getTime()) {
                return false;
            }
            long days = ((d1.getTime() - d2.getTime()) / 1000) / 60 / 60 / 24;
            return days <= 7;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
