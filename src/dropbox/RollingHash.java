package dropbox;

public class RollingHash {
    int prime = 105613, a, h = 1;
    long currHashValue;

    public RollingHash(int a, byte[] initialBytes) {
        this.a = a;
        for (int i = 0; i < initialBytes.length - 1; i++) {
            h = (h * a) % prime;
        }
        currHashValue = hash(initialBytes);
    }

    public long hash(byte[] bytes) {
        int hashVal = 0;
        for(int i = 0; i < bytes.length; i++) {
            hashVal = (a * hashVal + bytes[i]) % prime;
        }
        return hashVal;
    }

    public long recompute(byte removed, byte incoming) {
        currHashValue = (a * (currHashValue - removed * h) + incoming) % prime;
        if (currHashValue < 0) {
            currHashValue += prime;
        }
        return currHashValue;
    }
}
