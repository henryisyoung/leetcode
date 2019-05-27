package google;

public class KeyBoardMoving {
    public static String stepsFindWord(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        char cur = 'A';
        for (char c : s.toCharArray()){
            int curR = (cur - 'A') / 5, curC = (cur - 'A') % 5;
            int nextR = (c - 'A') / 5, nextC = (c - 'A') % 5;
            if (nextR > curR) {
                while (nextR > curR) {
                    nextR--;
                    sb.append("D");
                }
            } else {
                while (nextR < curR) {
                    nextR++;
                    sb.append("U");
                }
            }
            if (nextC > curC) {
                while (nextC > curC) {
                    nextC--;
                    sb.append("R");
                }
            } else {
                while (nextC < curC) {
                    nextC++;
                    sb.append("L");
                }
            }
            sb.append("!");
            cur = c;
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "CARS";
        String result = stepsFindWord(s);
        System.out.println(result); //RR!LL!RRDDD!R!
    }
}
