package reddit;

import com.fasterxml.jackson.databind.JsonNode;
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
public class ParseJson {
    //https://jenkov.com/tutorials/java-json/jackson-jsonnode.html
    public static void main(String[] args) {

        try {
//            String s = "{\"10-01-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":11,\"uniqueviews\":3}},\"10-02-2020\":{\"platform1\":{\"pageviews\":71,\"uniqueviews\":2},\"platform2\":{\"pageviews\":111,\"uniqueviews\":3}}}";
            String s = "{\"10-02-2020\":{\"platform1\":{\"pageviews\":12,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-03-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-04-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-05-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}},\"10-01-2020\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":1,\"uniqueviews\":3}}}";

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String,Map<String, Integer>>> map = objectMapper.readValue(s, Map.class);
            System.out.println(pastDaysPageviews("10-05-2020", map));
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public static int pastDaysPageviews(String date, Map<String, Map<String,Map<String, Integer>>> map) {
        int count = 0;

        TreeMap<String, Integer> pageViewCount = new TreeMap<>();
        for (String day : map.keySet()) {
            if (compareDate(date, day)) {
                int val = findPageViews(map.get(day));
                count += val;
                pageViewCount.put(day, val);
            }
        }
        int days = map.size();
        int[] nums = new int[days];
        int i = 0;
        int max = 0;

        System.out.println("pageViewCount " + pageViewCount);
        for (int c : pageViewCount.values()) {
            nums[i++] = c;
            max = Math.max(c, max);
        }
        int k = 3;
        if (nums.length <= k) return max;
        int[] array = maxWindow(nums, k);
        System.out.println(Arrays.toString(array));
        return count;
    }

    private static int[] maxWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            while (!deque.isEmpty() && nums[i] >= nums[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.add(i);
        }
        result[0] = nums[deque.peekFirst()];

        for (int i = k; i < n; i++) {
            int l = i - k;
            while (!deque.isEmpty() && l >= deque.peekFirst()) {
                deque.pollFirst();
            }
            while (!deque.isEmpty() && nums[i] >= nums[deque.peekLast()]) {
                deque.pollLast();
            }
            deque.add(i);
            result[i - k + 1] = nums[deque.peekFirst()];
        }
        return result;
    }

    private static int findPageViews(Map<String, Map<String, Integer>> map) {
        int count = 0;
        for (String platform : map.keySet()) {
            Map<String, Integer> platformMap = map.get(platform);
            for (String viewType : platformMap.keySet()) {
                if (viewType.equals("pageviews")) {
                    count += platformMap.get(viewType);
                }
            }
        }

        return count;
    }

    private static boolean compareDate(String date1, String date2) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            if (d1.getTime() < d2.getTime()) {
                return false;
            }
            long days = ((d1.getTime() - d2.getTime()) / 1000) / 60 / 60 / 24;
            return days < 7;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String mostViewsPlatform(JsonNode jsonNode, String date) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String   fieldName  = field.getKey();
            JsonNode fieldValue = field.getValue();

            System.out.println(fieldName + " = " + fieldValue);
            if (date.equals(fieldName)) {
                return maxViewedPlatform(fieldValue);
            }
        }
        return "";
    }

    private static String maxViewedPlatform(JsonNode jsonNode) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        Map<String, Integer> countMap = new HashMap<>();
        int max = 0;
        String result = "";
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String   platform  = field.getKey();
            JsonNode fieldValue = field.getValue();
            int pageviewsCount = fieldValue.get("pageviews").intValue();
            countMap.put(platform, countMap.getOrDefault(platform, 0) + pageviewsCount);
            if (countMap.get(platform) > max) {
                max = countMap.get(platform);
                result = platform;
            }
        }
        return result;
    }
}
