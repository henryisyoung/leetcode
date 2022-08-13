package google.vo.mianjing;

public class WrapText {
    // https://www.1point3acres.com/bbs/thread-910401-1-1.html
    public int wrapText(String text, int cols) {
        int n = text.length();
        int rows = 0;
        for (int start = 0; start < n; ) {
            int prevSpace = -1;
            rows++;
            int j = start;
            for (; j < start + cols && j < n; j++) {
                char c = text.charAt(j);
                if (j == start && c == '\n') {
                    continue;
                }
                if (c == ' ') {
                    prevSpace = j;
                } else if (c == '\n') {
                    j++;
                    break;
                } else if (j == start + cols - 1 && Character.isAlphabetic(c) && j + 1 < n && Character.isAlphabetic(text.charAt(j + 1))) {
                    if (prevSpace != -1) {
                        j = prevSpace + 1;
                        break;
                    }
                }
            }
            start = j;
        }
        return rows;
    }

    public int minRows(String text, int col1, int col2) {
        int[] m1 = minRowsFromIndex(text, 0, col1);
        int[] n1 = minRowsFromIndex(text, 0, col2);
        int high = Math.min(m1[0], n1[0]);
        int low = 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (isPossible(text, col1, col2, mid)) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    public int[] minRowsFromIndex(String sentence, int index, int cols) {
        int n = sentence.length();
        int start = index;
        int rows = 0;
        while (start < n) {
            rows++;
            start += cols;
            while (start > 0 && sentence.charAt(start % n) != ' ') {
                start--;
            }
            start++;
        }
        return new int[]{rows, start};
    }

    private boolean isPossible(String text, int col1, int col2, int mid) {
        for (int rows = 1; rows < mid; rows++) {
            int[] m1 = minRowsFromIndex(text, 0, col1);
            int[] n1 = minRowsFromIndex(text, m1[1], col2);
            if (m1[0] + n1[0] <= mid) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

    }
}
