package datastructure.backtracking;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class mongodb {
    public static void main(String[] args) {
        String json = "{\"a\":1,\"b\":true,\"c\":{\"d\":3,\"e\":\"test\",\"f\":{\"g\":3,\"h\":false}}}";
        String json2 = "{\"a\":1,\"b\":true,\"c\":{\"d\":3,\"e\":\"test\",\"f\":{\"g\":3,\"h\":{\"asd\":123,\"fggdfs\":true,\"asdsdfsdf\":123.1}}}}";
        System.out.println(reformatJson(json2));
    }

    public static String reformatJson(String json) {
        if (json == null || json.length() == 0) return "";
        Object obj= JSONValue.parse(json);
        JSONObject jsonObject = (JSONObject) obj;

        System.out.println(jsonObject.keySet());

        Iterator<String> keys = jsonObject.keySet().iterator();

        Map<String, Object> map = new HashMap();
        while(keys.hasNext()) {
            String key = keys.next();
            Map<String, Object> map1 = compressJson(key, jsonObject.get(key));
            for (Map.Entry<String, Object> entry : map1.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println(map);
        String jsonText = JSONValue.toJSONString(map);
        return jsonText;
    }

    private static Map<String, Object> compressJson(String key, Object obj) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                Iterator<String> childrenKeys = jsonObject.keySet().iterator();
                Map<String, Object> result = new HashMap();

                while(childrenKeys.hasNext()) {
                    String childKey = childrenKeys.next();
                    Map<String, Object> map = compressJson(childKey, jsonObject.get(childKey));
                    for (String mapKey : map.keySet()) {
                        String newKey = key + "." + mapKey;
                        result.put(newKey, map.get(mapKey));
                    }
                }
                return result;
            } else {
                Map<String, Object> map = new HashMap();
                map.put(key, obj);
                return map;
            }
    }

}
