package dropbox;

public class RollingHash {

    private final int LARGE_PRIME = 105613;
    private final int a;
    private int h=1;
    private final int WINDOW_LENGTH;

    long currHashValue;

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
        currHashValue = (a*(currHashValue - removed*h) + incoming)%LARGE_PRIME;

        // We might get negative value of t, converting it to positive
        if (currHashValue < 0)
            currHashValue += LARGE_PRIME;

        return currHashValue;
    }
}
