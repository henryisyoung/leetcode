package snowflake.mianjing;

/*
Problem Requirements
You are given a document. It is stored as a list of strings, where each string is one line of text.

You need to write a function that acts like a simple grep tool:

Search for lines that contain a specific target string.
If line i has the target, print that line. Also print linesAround number of lines before and after it.
Important: Do not print the same line twice.
Keep the lines in the same order as the original document.
The interview usually follows these steps:

Solve for a standard list input.
Solve for streaming input (lines come in one by one).
Optimize the speed.
Design a solution using multithreading.
Example
lines = [
    "good morning",
    "hello there",
    "my name is Alex",
    "my friend is albert",
    "it is nice to meet you Alex",
]

search_target = "Alex"
lines_around = 1
Expected Output:

[
    "hello there",
    "my name is Alex",
    "my friend is albert",
    "it is nice to meet you Alex",
]
Note: The line "my friend is albert" is near both matches, but it is included in the result only once.

Part 2: Follow-up (Streaming Input)
Problem Adjustments
Now, the lines come in one at a time. You cannot see the whole list at the start. You must process each line as it arrives.

Solution Design
We need to remember recent lines to handle the "context before" a match. We also need to know when to print lines for the "context after" a match.

Buffer: Use a deque to store the last k lines. This handles the "before" context.
Track Printing: Use a variable emit_until. This tells us the furthest index into the future we need to print.
Avoid Duplicates: Check a flag on each buffered line to ensure we don't print it twice.
This method uses O(k) memory.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class GrepWithContextLines {

    public List<String> findLines(String word, List<String> doc, int k) {
        List<String> result = new ArrayList<>();
        int n = doc.size();
        if (n == 0) return result;

        List<int[]> intervals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (doc.get(i).contains(word)) {
                int start = Math.max(0, i - k);
                int end = Math.min(n - 1, i + k);
                intervals.add(new int[]{start, end});
            }
        }
        if (intervals.isEmpty()) return result;

        List<int[]> merged = new ArrayList<>();
        int[] prev = intervals.get(0);
        for (int i = 1; i < intervals.size(); i++) {
            int[] cur = intervals.get(i);
            if (cur[0] > prev[1] + 1) {
                merged.add(prev);
                prev = cur;
            } else {
                prev[1] = Math.max(prev[1], cur[1]);
            }
        }
        merged.add(prev);

        for (int[] inter : merged) {
            for (int i = inter[0]; i <= inter[1]; i++) {
                result.add(doc.get(i));
            }
        }
        return result;
    }

    /*
    Part 2: Streaming Input
    -----------------------
    Lines arrive one at a time via process(line). We must emit lines in order,
    never duplicate a line, and respect the k-line "before"/"after" context
    around any line containing the target word.

    Approach (O(k) extra memory):
      - buffer  : a deque holding the most recent UN-EMITTED lines, capped at k.
                  These are the candidates for "before" context of a future match.
      - emitUntil : the largest line index that still must be emitted because it
                    falls inside the "after" window of a recent match.
      - lastEmittedIdx : guards against duplicates (lines already pushed out).

    For each incoming line at index i:
      1) If it contains the target:
           - Flush every buffered line whose index > lastEmittedIdx (the "before"
             context).
           - Emit the current line.
           - Extend emitUntil to i + k (so the next k non-matching lines also
             get emitted as "after" context).
      2) Else if i <= emitUntil:
           - Emit it directly (still inside an "after" window).
      3) Else:
           - Push it into the buffer; if the buffer grows past k, drop the oldest.
     */
    public static class StreamingGrep {
        private final String target;
        private final int k;
        private final Deque<String> buffer = new ArrayDeque<>();
        private final List<String> output = new ArrayList<>();
        private int curIdx = -1;
        private int emitUntil = -1;
        private int lastEmittedIdx = -1;

        public StreamingGrep(String target, int k) {
            this.target = target;
            this.k = k;
        }

        public void process(String line) {
            curIdx++;
            if (line.contains(target)) {
                // Flush buffered "before" context. Buffer holds the last
                // buffer.size() un-emitted lines, ending at index curIdx - 1.
                int idx = curIdx - buffer.size();
                for (String s : buffer) {
                    if (idx > lastEmittedIdx) {
                        output.add(s);
                        lastEmittedIdx = idx;
                    }
                    idx++;
                }
                buffer.clear();

                output.add(line);
                lastEmittedIdx = curIdx;
                emitUntil = curIdx + k;
            } else if (curIdx <= emitUntil) {
                output.add(line);
                lastEmittedIdx = curIdx;
            } else {
                buffer.addLast(line);
                if (buffer.size() > k) {
                    buffer.pollFirst();
                }
            }
        }

        public List<String> getOutput() {
            return output;
        }
    }

    public static void main(String[] args) {
        // Sanity check vs. the batch solution from Part 1.
        List<String> lines = Arrays.asList(
                "good morning",
                "hello there",
                "my name is Alex",
                "my friend is albert",
                "it is nice to meet you Alex"
        );
        String target = "Alex";
        int k = 1;

        StreamingGrep grep = new StreamingGrep(target, k);
        for (String line : lines) {
            grep.process(line);
        }
        System.out.println("Streaming : " + grep.getOutput());
        System.out.println("Batch     : " + new GrepWithContextLines().findLines(target, lines, k));
    }
}
