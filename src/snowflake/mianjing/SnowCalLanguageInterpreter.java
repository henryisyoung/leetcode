package snowflake.mianjing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Problem Overview
SnowCal is a simple coding language. It stores exactly one integer, X, in memory. The value of X always starts at 0.

Available Commands
The language uses the following instructions:

ADD Y: Add the value Y to X.
MUL Y: Multiply X by the value Y.
FUN F: Start defining a new function named F.
END: Finish defining the current function.
INV F: Call (invoke) the function named F.
Language Rules
Every function has a unique name.
You cannot define one function inside another (no nested functions).
You can define a function but never use it.
If the code contains INV F, the function F is guaranteed to exist.
Your Goal: You are given a list of instructions. Run the program and return the final value of X.

Important: Commands inside a function (FUN ... END) do not run immediately. They only change X when that function is called using INV.

Sample Cases
Case 1

Input: program = ["MUL 2", "ADD 3"]

Output: 3

Explanation:

X starts at 0.
MUL 2: 0 * 2 equals 0.
ADD 3: 0 + 3 equals 3.
Case 2

Input: program = ["FUN INCREMENT", "ADD 1", "END", "INV INCREMENT", "MUL 2", "ADD 3"]

Output: 5

Case 3

Input: program = ["FUN INCREMENT", "ADD 1", "END", "FUN INCREMENT2", "ADD 1", "MUL 2", "END", "MUL 2", "INV INCREMENT2", "ADD 3", "INV INCREMENT"]

Output: 6

Input Limits
The list of program lines is between 1 and 10,000 items long.
Each line is one of the valid commands listed above.
Y is an integer between -1,000,000,000 and 1,000,000,000.
There are no nested functions.
 */
public class SnowCalLanguageInterpreter {

    private long x;
    private Map<String, List<String>> funcs;

    public int calculator(String[] programs) {
        x = 0;
        funcs = new HashMap<>();
        List<String> top = new ArrayList<>();

        int i = 0;
        while (i < programs.length) {
            String line = programs[i];
            if (line.startsWith("FUN")) {
                String name = line.split(" ")[1];
                List<String> body = new ArrayList<>();
                i++;
                while (i < programs.length && !programs[i].startsWith("END")) {
                    body.add(programs[i]);
                    i++;
                }
                funcs.put(name, body);
                i++; // skip END
            } else {
                top.add(line);
                i++;
            }
        }

        run(top);
        return (int) x;
    }

    private void run(List<String> body) {
        for (String line : body) {
            if (line.startsWith("ADD")) {
                x += Long.parseLong(line.split(" ")[1]);
            } else if (line.startsWith("MUL")) {
                x *= Long.parseLong(line.split(" ")[1]);
            } else if (line.startsWith("INV")) {
                String name = line.split(" ")[1];
                run(funcs.get(name));
            }
        }
    }
}
