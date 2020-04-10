package facebook;

public class ReadNCharactersGivenRead4 {
    public int read(char[] buf, int n) {
        int readBytes = 0;
        char[] readBuffer = new char[4];
        boolean end = false;
        while (!end && readBytes < n) {
            int curRead = read4(readBuffer);
            if (curRead != 4) end = true;
            int addBytes = Math.min(curRead, n - readBytes);
            System.arraycopy(readBuffer, 0, buf, readBytes, addBytes);
            readBytes += addBytes;
        }
        return readBytes;
    }

    int read4(char[] buf){
        return -1;
    }
}
