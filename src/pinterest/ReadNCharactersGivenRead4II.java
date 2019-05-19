package pinterest;

public class ReadNCharactersGivenRead4II {
    int readPos = 0, writePos = 0;
    char[] buff = new char[4];
    public int read(char[] buf, int n) {
        int i = 0;
        while (i < n && (readPos < writePos || (readPos = 0) < (writePos = read4(buff)))) {
            buf[i++] = buff[readPos++];
        }
        return i;
    }

    public int read2(char[] buf, int n) {
        for (int i = 0; i < n; ++i) {
            if (readPos == writePos) {
                writePos = read4(buff);
                readPos = 0;
                if (writePos == 0) {
                    return i;
                }
            }
            buf[i] = buff[readPos++];
        }
        return n;
    }

    private int read4(char[] buffer) {
        return 0;
    }
}
