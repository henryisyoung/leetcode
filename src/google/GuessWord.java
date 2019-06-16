package google;

import java.util.*;

public class GuessWord {
     static class Master {
         String secret;
         public Master(String secret) {
             this.secret = secret;
         }

         public int guess(String word){
             int res = 0;
             for(int i = 0 ; i < word.length() ; i++) {
                 if(word.charAt(i) == secret.charAt(i)) {
                     res++;
                 }
             }
             return res;
         }
     }

    public static void findSecretWord(String[] wordlist, Master master) {
        List<String> list = new ArrayList<>();
        for(String str: wordlist) {
            list.add(str);
        }
        for(int i = 0 ; i < 10 ; i++) {
            Map<String, Integer> zeroMatch = new HashMap<>();
            for(String s1: list) {
                zeroMatch.putIfAbsent(s1, 0);
                for(String s2: list) {
                    if(match(s1, s2) == 0) {
                        zeroMatch.put(s1, zeroMatch.get(s1) + 1);
                    }
                }
            }
            Pair pair = new Pair("", 101);  // list size is 100
            for(String key : zeroMatch.keySet()) {
                if(zeroMatch.get(key) < pair.freq) {
                    pair = new Pair(key, zeroMatch.get(key));
                }
            }
            int matchNum = master.guess(pair.key);
            if(matchNum == 6) return;
            List<String> tmp = new ArrayList<>();
            for(String s: list) {
                if(match(s, pair.key) == matchNum) {
                    tmp.add(s);
                }
            }
            list = tmp;
        }
    }
    private static class Pair {
        String key;
        int freq;
        public Pair(String key, int freq) {
            this.key = key;
            this.freq = freq;
        }
    }

    private static int match(String s1, String s2) {
        int res = 0;
        for(int i = 0 ; i < s1.length() ; i++) {
            if(s1.charAt(i) == s2.charAt(i)) {
                res++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String secret = "acckzz";
        String[] wordlist = {"acckzz","ccbazz","eiowzz","abcczz"};
        Master master = new Master(secret);
        findSecretWord(wordlist, master);
    }
}
