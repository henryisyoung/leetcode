package airbnb;

import java.util.*;

public class SimulateDiplomacy {
    public List<String> solveDiplomay(List<String> input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.size() == 0) {
            return result;
        }

        Map<String, List<String>> posMap = new HashMap<>();
        Map<String, String> resMap = new HashMap<>();
        Map<String, Integer> strengthMap = new HashMap<>();

        for (String line : input) {
            String[] parse = line.split(" ");
            String army = parse[0];
            strengthMap.put(army, 1);

            String pos = "";
            if (parse[2].equals("Hold") || parse[2].equals("Support")) {
                pos = parse[1];
            }
            if (parse[2].equals("Move")){
                pos = parse[3];
            }
            if (!posMap.containsKey(pos)) {
                posMap.put(pos, new ArrayList<String>());
            }
            posMap.get(pos).add(army);
        }

        for (String line : input) {
            String[] parse = line.split(" ");
            if (parse[2].equals("Support")) {
                String supportPos = parse[1], supportTo = parse[3];
                if (posMap.containsKey(supportPos) && posMap.get(supportPos).size() == 1) {
                    // no disturb while supporting
                    strengthMap.put(supportTo, strengthMap.get(supportTo) + 1);
                }
            }
        }

        for (String pos : posMap.keySet()) {
            List<String> armyList = posMap.get(pos);

            if (armyList.size() == 1) {
                resMap.put(armyList.get(0), pos);
            } else {
                int maxStrength = 0;
                String win = "";
                for (String army : armyList) {
                    int curStrength = strengthMap.get(army);
                    if (curStrength > maxStrength) {
                        if (win.length() > 0) {
                            resMap.put(win, "[dead]");
                        }
                        maxStrength = curStrength;
                        win = army;
                        resMap.put(army, pos);
                    } else if (curStrength == maxStrength) {
                        resMap.put(army, "[dead]");
                        resMap.put(win, "[dead]");
                    } else if (curStrength < maxStrength) {
                        resMap.put(army, "[dead]");
                    }
                }
            }
        }
        for (String armyItem : resMap.keySet()) {
            result.add(armyItem + " " + resMap.get(armyItem));
        }
        return result;
    }

    public static void main(String[] args) {
        List<String> input = Arrays.asList("A Mu Support B", "B Bo Move Pr", "C Pr Hold", "D Wa Move Mu");
        List<String> input2 = Arrays.asList("A Mu Hold", "B Wa Move Bo");
        List<String> input3 = Arrays.asList("A Mu Hold", "B Bo Move Mu", "C Wa Support B");
        List<String> input4 = Arrays.asList("A Mu Hold", "B Bo Move Mu", "C Pr Move Mu", "D Wa Hold");
        List<String> input5 = Arrays.asList("A Mu Support B", "B Oa Move Mu");
        SimulateDiplomacy solver = new SimulateDiplomacy();
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
