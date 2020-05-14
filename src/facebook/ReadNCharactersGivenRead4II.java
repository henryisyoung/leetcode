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
    public int read4000(char[] buf, int n) {
        int readCount = 0;
        while (!queue.isEmpty() && readCount < n) {
            buf[readCount++] = queue.poll();
        }
        if (readCount == n) return readCount;
        boolean end = false;
        while (!end && readCount < n) {
            char[] buffer = new char[4000];
            int curRead = read4(buffer);
            if (curRead != 4000) end = true;
            int add = Math.min(curRead, n - readCount);
            System.arraycopy(buffer, 0, buf, readCount, add);
            readCount += add;
            if (readCount == n && add < curRead) {
                int i = 0;
                while (add + i < curRead) {
                    queue.add(buffer[add + i++]);
                }
            }
        }
        return readCount;
    }
}
