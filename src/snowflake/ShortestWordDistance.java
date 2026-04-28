package snowflake;

public class ShortestWordDistance {
    public int shortestDistance(String[] wordsDict, String word1, String word2) {
        int minDist = Integer.MAX_VALUE, word1Prev = -1, word2Prev = -1;

        for (int i = 0 ; i < wordsDict.length; i ++) {
            if (wordsDict[i].equals(word1)) {
                if (word2Prev != -1) {
                    minDist = Math.min(i - word2Prev, minDist);
                }
                word1Prev = i;
            } else if (wordsDict[i].equals(word2)) {
                if (word1Prev != -1) {
                    minDist = Math.min(i - word1Prev, minDist);
                }
                word2Prev = i;
            }
        }

        return minDist;
    }
}
