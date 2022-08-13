package roblox.onsite;

import java.util.*;

public class RandomString {
    static class Node {
        String string;
        int sumWeight;
        public Node(String string, int sumWeight) {
            this.string = string;
            this.sumWeight = sumWeight;
        }
    }
    int sumWeight;
    TreeMap<Integer, String> stringWeightMap;
    Random random;
    List<Node> list;
    public RandomString(Map<String, Integer> weightMap) {
        this.sumWeight = 0;
        this.stringWeightMap = new TreeMap<>();
        this.list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : weightMap.entrySet()) {
            String str = entry.getKey();
            int weight = entry.getValue();
            list.add(new Node(str, sumWeight));
            stringWeightMap.put(sumWeight, str);
            sumWeight += weight;
        }
        this.random = new Random();
    }

    public String generateStr() {
        int wight = random.nextInt(sumWeight);
        return stringWeightMap.floorEntry(wight).getValue();
    }

    public static String generateStr2(List<Node> list, int weight) {
//        Random random = new Random();
//        int wight = random.nextInt(10);
        int l = 0, r = list.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            int midWeight = list.get(mid).sumWeight;
            if (midWeight == weight) {
                return list.get(mid).string;
            }else if (midWeight > weight) {
                r = mid;
            } else {
                l = mid;
            }
        }
        int sumWeight = list.get(r).sumWeight;
        if (sumWeight <= weight) {
            return list.get(r).string;
        }
        return list.get(l).string;
    }

    public static void main(String[] args) {
        //a 3, b 1, c 6
        List<Node> list = Arrays.asList(
                new Node("a", 0),  // 0 - 2
                new Node("b", 3),  // 3
                new Node("c", 4)   // 4 - 9
        );
        for (int i = 0; i < 10; i++) {
            System.out.println(generateStr2(list, i));
        }
    }
}
