package airbnb.New2026;
/*
Parse URL Query Parameters into a Map.

Given a URL string `url`, parse its query string and return all query
parameters as a key -> value map.

Rules
  - The query string begins after the FIRST '?'.
  - Each parameter is key=value, separated by '&'.
  - A bare key without '=' maps to Boolean.TRUE.
    (Some interviewers prefer ""; clarify before coding.)
  - A key with '=' but empty value maps to "".
  - No '?' or empty query -> empty map.
  - Duplicate keys collect values into a List.

I/O
  Input : url (String)
  Output: Map<String,Object>

Constraints
  1 <= url.length() <= 1e5

Examples
  "https://example.com/path?foo=1&bar=2"      -> {foo=1, bar=2}
  "https://example.com/path"                  -> {}
  "https://example.com/path?"                 -> {}
  "?a=b&c=d"                                  -> {a=b, c=d}
  "?sd"                                       -> {sd=true}
  "?a=b&a=c&a=d"                              -> {a=[b, c, d]}
  "?key=hello%20world"                        -> {key=hello%20world}
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
Implementation notes

  * Single pass over the query substring. We track the start of the current
    parameter and the position of the '=' (if any) inside it. On '&' (or
    end-of-string) we materialise one (key, value) pair.

  * Repeated keys are folded into a List while preserving encounter order:
        a=b&a=c -> {a=[b, c]}

Complexity
  Time:   O(n)   single pass + O(n) total for substring allocations
  Memory: O(n)   for the returned map's keys/values
*/
public class ParseUrlQueryParams2 {

    /** Default parser: percent-decodes, bare keys map to Boolean.TRUE. */
    public Map<String, Object> parse(String url) {
        Map<String, Object> result = new HashMap<>();
        if(url == null || url.length() == 0) return result;

        int index = url.indexOf("?");
        if(index == -1 || index == url.length() - 1) return result;

        String str = url.substring(index + 1);
        int n = str.length(), i = 0;
        int start = 0, eq = -1;

        while(i <= n) {
            if(i == n || str.charAt(i) == '&') {
                if(i != start) {
                    if(eq != -1) {
                        String key = str.substring(start, eq);
                        String value = str.substring(eq + 1, i);
                        addMap(result, key, value);
                    } else {
                        String key = str.substring(start, i);
                        Boolean value = true;
                        addMap(result, key, value);
                    }
                }
                eq = -1;
                start = i + 1;
            } else if(str.charAt(i) == '=') {
                eq = i;
            }
            i++;
        }

        return result;
    }

    private void addMap(Map<String, Object> map, String key, Object value) {
        if(!map.containsKey(key)) {
            map.put(key, value);
        } else if(map.get(key) instanceof List<?>) {
            List<Object> list = (List<Object>) map.get(key);
            list.add(value);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(map.get(key));
            list.add(value);
            map.put(key, list);
        }
    }
    
    public static void main(String[] args) throws IOException {
        ParseUrlQueryParams2 solver = new ParseUrlQueryParams2();

        Map<String, Object> actual = solver.parse("?a=b&a=c&a=d&k");

        System.out.println(actual);
    }
}
