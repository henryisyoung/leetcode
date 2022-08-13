package google.vo;

public class ShortestWayFormString {
    public int shortestWay(String source, String target) {
        int i = 0, n = target.length(), m = source.length();
        int count = 0;
        while(i < n) {
            int prevI = i;
            for(int j = 0; j < m; j++) {
                if(i < n && target.charAt(i) == source.charAt(j)) {
                    i++;
                }
            }
            if(prevI == i) {
                return -1;
            }
            count++;
        }

        return count;
    }
}
