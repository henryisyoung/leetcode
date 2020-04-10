package facebook;

import java.util.LinkedList;
import java.util.Queue;

public class ReadNCharactersGivenRead4II {
    Queue<Character> queue = new LinkedList<>();
    public int read(char[] buf, int n) {
        int readBytes = 0;
        while (!queue.isEmpty() && readBytes < n) {
            buf[readBytes++] = queue.poll();
        }
        if (readBytes == n) return readBytes;
        boolean end = false;
        char[] buffer = new char[4];
        while (!end && readBytes < n) {
            int curRead = read4(buffer);
            if (curRead != 4) end = true;
            int addBytes = Math.min(curRead, n - readBytes);
            System.arraycopy(buffer, 0, buf, readBytes, addBytes);
            readBytes += addBytes;
            if (readBytes == n && addBytes < curRead) {
                for (int i = 0; i + addBytes < curRead; i++) {
                    queue.add(buffer[i + addBytes]);
                }
            }
        }
        return readBytes;
    }

    int read4(char[] buf){
        return -1;
    }
}
