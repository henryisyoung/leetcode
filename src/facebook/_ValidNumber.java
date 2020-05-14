package facebook;

public class _ValidNumber {
    public boolean isNumber(String s) {
        if (s == null) return false;

        s = s.trim();
        int n = s.length();

        if (n == 0) return false;

        // flags
        int signCount = 0;
        boolean hasE = false;
        boolean hasNum = false;
        boolean hasPoint = false;

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            // invalid character
            if (!isValid(c)) return false;

            // digit is always fine
            if (c >= '0' && c <= '9') hasNum = true;

            // e or E
            if (c == 'e' || c == 'E') {
                // e cannot appear twice and digits must be in front of it
                if (hasE || !hasNum) return false;
                // e cannot be the last one
                if (i == n - 1) return false;

                hasE = true;
            }

            // decimal place
            if (c == '.') {
                // . cannot appear twice and it cannot appear after e
                if (hasPoint || hasE) return false;
                // if . is the last one, digits must be in front of it, e.g. "7."
                if (i == n - 1 && !hasNum) return false;

                hasPoint = true;
            }

            // signs
            if (c == '+' || c == '-') {
                // no more than 2 signs
                if (signCount == 2) return false;
                // sign cannot be the last one
                if (i == n - 1) return false;
                // sign can appear in the middle only when e appears in front
                if (i > 0 && !hasE) return false;

                signCount++;
            }
        }

        return true;
    }

    boolean isValid(char c) {
        return c == '.' || c == '+' || c == '-' || c == 'e' || c == 'E' || c >= '0' && c <= '9';
    }
}
