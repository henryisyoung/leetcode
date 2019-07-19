package dropbox;

import java.io.*;
import java.util.*;

public class FindByteInFile {

    public boolean containsBytesRollingHash(byte[] pattern, byte[] text) {
        if(text.length<pattern.length)
            return false;

        int m = pattern.length, n=text.length;
        byte[] initialBytes = Arrays.copyOfRange(text, 0, m);
        RollingHash hashFun = new RollingHash(31, initialBytes);

        long patternHashVal = hashFun.hash(pattern);
        for(int i=0; i<=n-m; i++) {
            if(patternHashVal==hashFun.currHashValue) {
                //need to check byte by byte to ensure
                int j=0;
                while(j<m && pattern[j]==text[i+j]) j++;
                if(j==m) return true;
            }

            if(i<n-m){
                hashFun.recompute(text[i], text[i+m]);
            }
        }

        return false;
    }

    public boolean containsBytesFileRollingHash (byte[] pattern, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        try {
            byte[] initialBytes = new byte[pattern.length];

            if (fis.read(initialBytes) != pattern.length) return false;

            RollingHash hashFun = new RollingHash(31, initialBytes);
            long patternHashVal = hashFun.hash(pattern);
            if (patternHashVal == hashFun.currHashValue) return true;

            Queue<Byte> window = new LinkedList<>();
            for (int i = 0; i < initialBytes.length; i++) {
                window.offer(initialBytes[i]);
            }

            int b;
            while ((b = fis.read()) != -1) {
                System.out.println(b);

                hashFun.recompute(window.poll(), (byte) b);
                window.offer((byte) b);
                if (patternHashVal == hashFun.currHashValue) return true;
            }

        }finally {
            fis.close();
        }
        return false;
    }
}
