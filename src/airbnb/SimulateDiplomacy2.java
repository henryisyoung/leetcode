package airbnb;

import java.util.*;

public class SimulateDiplomacy2 {
    public List<String> solveDiplomay(List<String> input) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> posMap = new HashMap<>();
        Map<String, Integer> powerMap = new HashMap<>();
        Map<String, String> resultMap = new HashMap<>();
        for (String line : input) {
            String[] arr = line.split(" ");
            String army = arr[0], startPlace = arr[1], action = arr[2];
            powerMap.put(army, 1);
            String dest = "";
            if (action.equals("Support") || action.equals("Hold")) {
                dest = startPlace;
            } else if (action.equals("Move")) {
                dest = arr[3];
            }
            if (!posMap.containsKey(dest)) {
                posMap.put(dest, new ArrayList<String>());
            }
            posMap.get(dest).add(army);
        }

        for (String line : input) {
            String[] arr = line.split(" ");
            String startPlace = arr[1], action = arr[2];
            if (action.equals("Support")) {
                if (posMap.containsKey(startPlace) && posMap.get(startPlace).size() == 1) {
                    String supportArmy = arr[3];
                    powerMap.put(supportArmy, powerMap.get(supportArmy) + 1);
                }
            }
        }


        for (String place : posMap.keySet()) {
            List<String> armies = posMap.get(place);

            if (armies.size() == 1) {
                resultMap.put(armies.get(0), place);
            } else {
                String winner = "";
                int maxPower = 0;
                for (String army : armies) {
                    int curPower = powerMap.get(army);
                    if (curPower > maxPower) {
                        if (winner.length() > 0) {
                            resultMap.put(winner, "[dead]");
                        }
                        winner = army;
                        maxPower = curPower;
                        resultMap.put(army, place);
                    } else if (curPower == maxPower) {
                        resultMap.put(winner, "[dead]");
                        resultMap.put(army, "[dead]");
                    } else {
                        resultMap.put(army, "[dead]");
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
