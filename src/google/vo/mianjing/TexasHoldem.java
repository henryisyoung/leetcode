package google.vo.mianjing;

import java.util.*;

public class TexasHoldem {
    public static List<List<int[]>> finalRes = new ArrayList<>();

    public boolean isFourHands(int[][] cards) {
        if (cards == null || cards.length == 0 || cards.length != 20) {
            return false;
        }
        Set<Integer>[] sortedCards = new Set[15];
        for (int i = 0; i < 15; i++) {
            sortedCards[i] = new HashSet<>();
        }
        for (int[] card : cards) {
            sortedCards[card[0]].add(card[1]);
        }
        List<List<int[]>> res = new ArrayList<>();
        List<int[]> curHand = new ArrayList<>();
        return canBreakFourHands(0, sortedCards, curHand, res);
    }

    private boolean canBreakFourHands(int hand, Set<Integer>[] sortedCards, List<int[]> curHand, List<List<int[]>> res) {
        if (hand == 4) {
            finalRes = new ArrayList<>(res);
            return true;
        }
        for (int i = 2; i < 15; i++) {
            if (sortedCards[i].isEmpty()) {
                continue;
            }
            if (sortedCards[i].size() == 4) {
                Set<Integer> colors = new HashSet<>(sortedCards[i]); // MUST DEEP COPY
                for (int color : colors) {
                    curHand.add(new int[]{i, color});
                }
                res.add(new ArrayList<>(curHand));
                curHand.clear();
                sortedCards[i].clear();
                if (canBreakFourHands(hand + 1, sortedCards, curHand, res)) {
                    return true;
                }
                sortedCards[i] = colors;
                res.remove(res.size() - 1);
            }
            if (i + 4 < 15) {
                Set<Integer> colors = new HashSet<>(sortedCards[i]);
                for (int color : colors) {
                    if (sortedCards[i + 1].contains(color) && sortedCards[i + 2].contains(color) && sortedCards[i + 3].contains(color) && sortedCards[i + 4].contains(color)) {
                        for (int j = 0; j < 5; j++) {
                            curHand.add(new int[]{i + j, color});
                            sortedCards[i + j].remove(color);
                        }
                        res.add(new ArrayList<>(curHand));
                        curHand.clear();
                        if (canBreakFourHands(hand + 1, sortedCards, curHand, res)) {
                            return true;
                        }
                        for (int j = 0; j < 5; j++) {
                            sortedCards[i + j].add(color);
                        }
                    }
                }
            }
        }
        return false;
    }
}
