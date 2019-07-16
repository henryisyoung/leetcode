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

    public boolean containsBytesFileRollingHash (byte[] pattern, File file) throws FileNotFoundException, IOException {
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

    class RollingHash {
        private final int LARGE_PRIME = 105613;
        private final int a;
        private int h=1;
        private final int WINDOW_LENGTH;

        private long currHashValue;

        public RollingHash(int a, byte[] initialBytes) {
            this.a = a;
            this.WINDOW_LENGTH = initialBytes.length;

            // The value of h would be "pow(a, WINDOW_LENGTH-1)%q
            for (int i = 0; i < WINDOW_LENGTH-1; i++) {
                //a^n % p = (a^n-1 % p * a%p)%p;
                h = (h*a)%LARGE_PRIME;
            }

            currHashValue = hash(initialBytes);

        }

        public long hash(byte[] bytes) {
            int hashVal=0;

            for(int i=0; i<bytes.length; i++) {
                hashVal = (a*hashVal + bytes[i])% LARGE_PRIME;
            }

            return hashVal;
        }

        public long recompute(byte removed, byte incoming) {

            //(a+b) %p = (a%p + b%p) %p
            //(a-b) %p = (a%p - b%p) %p might give negative
            //(a*b) %p = (a%p * b%p) %p
            currHashValue = (a * (currHashValue - removed * h) + incoming) % LARGE_PRIME;

            // We might get negative value of t, converting it to positive
            if (currHashValue < 0)
                currHashValue += LARGE_PRIME;

            return currHashValue;
        }
    }
}
