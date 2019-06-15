package leetcode.dataStructrue;

import java.util.*;

public class TinyUrl {
    Integer GLOBAL_ID;
    Map<String, Integer> urlToId;
    Map<Integer, String> IdToUrl;
    public TinyUrl() {
        this.GLOBAL_ID = 0;
        this.IdToUrl = new HashMap<>();
        this.urlToId = new HashMap<>();
    }
    public String longToShort(String url) {
        if (urlToId.containsKey(url)) {
            return "http://tiny.url/" + idConvertUrl(urlToId.get(url));
        }
        GLOBAL_ID++;
        urlToId.put(url, GLOBAL_ID);
        IdToUrl.put(GLOBAL_ID, url);
        return "http://tiny.url/" + idConvertUrl(GLOBAL_ID);
    }

    private String idConvertUrl(int id) {
        String result = "";
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (id > 0) {
            int index = id % 62;
            result = chars.charAt(index) + result;
            id /= 62;
        }
        while (result.length() < 6) {
            result = "0" + result;
        }
        return result;
    }

    public String shortToLong(String url) {
        // write your code here
        String urlKey = url.substring("http://tiny.url/".length());
        int id = urlConvertId(urlKey);
        if (!IdToUrl.containsKey(id)) {
            return null;
        }
        return IdToUrl.get(id);
    }

    private int urlConvertId(String url) {
        int result = 0;
        for (int i = 0; i < url.length(); i++) {
            result = result * 62 + charToInt(url.charAt(i));
        }
        return result;
    }

    private int charToInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 10;
        } else {
            return c - 'A' + 36;
        }
    }
}
