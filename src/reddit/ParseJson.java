package reddit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            String s = "{\"date1\":{\"platform1\":{\"pageviews\":7,\"uniqueviews\":2},\"platform2\":{\"pageviews\":11,\"uniqueviews\":3}},\"date2\":{\"platform1\":{\"pageviews\":71,\"uniqueviews\":2},\"platform2\":{\"pageviews\":111,\"uniqueviews\":3}}}";

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(s);
            System.out.println(mostViewsPlatform(jsonNode, "date2"));

        } catch (Exception e) {
            System.out.println(e.getCause());
        }
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
