package airbnb;

import java.util.*;

public class SimulateDiplomacy2 {
    public List<String> solveDiplomay(List<String> input) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> posMap = new HashMap<>();
        Map<String, Integer> strengthMap = new HashMap<>();
        Map<String, String> resultMap = new HashMap<>();
        for (String item : input) {
            String[] arr = item.split(" ");
            String pos = "", army = arr[0];
            String action = arr[2];
            strengthMap.put(army, 1);
            if (action.equals("Support") || action.equals("Hold")) {
                pos = arr[1];
            } else if (action.equals("Move")) {
                pos = arr[3];
            }
            if (!posMap.containsKey(pos)) {
                posMap.put(pos, new ArrayList<>());
            }
            posMap.get(pos).add(army);
        }
        for (String item : input) {
            String[] arr = item.split(" ");
            String pos = "", army = arr[0];
            String action = arr[2];
            if (action.equals("Support")) {
                pos = arr[1];
                if (posMap.containsKey(pos) && posMap.get(pos).size() == 1) {
                    String supportTo = arr[3];
                    strengthMap.put(supportTo, strengthMap.get(supportTo) + 1);
                }
            }
        }

        for (String pos : posMap.keySet()) {
            List<String> armies = posMap.get(pos);
            if (armies.size() == 1) {
                resultMap.put(armies.get(0), pos);
            } else {
                int maxStrength = 0;
                String winner = "";
                for (String army : armies) {
                    int curStrength = strengthMap.get(army);
                    if (curStrength > maxStrength) {
                        maxStrength = curStrength;
                        if (winner.length() > 0) {
                            resultMap.put(winner, "[dead]");
                        }
                        winner = army;
                        resultMap.put(army, pos);
                    } else if (curStrength == maxStrength) {
                        resultMap.put(winner, "[dead]");
                        resultMap.put(army, "[dead]");
                    } else {
                        resultMap.put(winner, "[dead]");
                    }
                }
            }
        }
        for (String army : resultMap.keySet()) {
            result.add(army + " " + resultMap.get(army));
        }
        return result;
    }

    public static void main(String[] args) {
        List<String> input = Arrays.asList("A Mu Support B", "B Bo Move Pr", "C Pr Hold", "D Wa Move Mu");
        List<String> input2 = Arrays.asList("A Mu Hold", "B Wa Move Bo");
        List<String> input3 = Arrays.asList("A Mu Hold", "B Bo Move Mu", "C Wa Support B");
        List<String> input4 = Arrays.asList("A Mu Hold", "B Bo Move Mu", "C Pr Move Mu", "D Wa Hold");
        List<String> input5 = Arrays.asList("A Mu Support B", "B Oa Move Mu");
        SimulateDiplomacy2 solver = new SimulateDiplomacy2();
        for (String l : solver.solveDiplomay(input)) {
            System.out.println(l);
        }
        System.out.println();
        for (String l : solver.solveDiplomay(input2)) {
            System.out.println(l);
        }
        System.out.println();
        for (String l : solver.solveDiplomay(input3)) {
            System.out.println(l);
        }
        System.out.println();
        for (String l : solver.solveDiplomay(input4)) {
            System.out.println(l);
        }
        System.out.println();
        for (String l : solver.solveDiplomay(input5)) {
            System.out.println(l);
        }
    }
}
