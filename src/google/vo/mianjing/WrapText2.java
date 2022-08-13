package google.vo.mianjing;

public class WrapText2 {
    // https://www.1point3acres.com/bbs/thread-910401-1-1.html
    public int wrapText(String text, int cols) {
        int n = text.length();
        int start = 0;
        int rows = 0;
        while (start < n) {
            int prevSpace = -1;
            int j = start;
            rows++;
            for (; j < start + cols; j++) {
                char c = text.charAt(j);
                if (c == ' ') {
                    prevSpace = j;
                } else if (c == '\n' && c != start) {
                    j++;
                    break;
                }
                if (j == start + cols - 1 && Character.isAlphabetic(c) && j + 1 < n && Character.isAlphabetic(text.charAt(j + 1))) {
                    if (prevSpace == -1) {
                        continue;
                    } else {
                        j = prevSpace + 1;
                        break;
                    }
                }
            }
            start = j;
        }
        return rows;
    }

    public static void main(String[] args) {
        int i = 0;
        for (; i < 3; i++) {
            if (i == 2) {
                continue;
            }
        }
        System.out.println(i);
    }
}
