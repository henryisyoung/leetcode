package snowflake.mianjing.selfreview;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class StreamingGrep {
    int curId, utilId, lastId;
    Deque<String> buffer;
    List<String> output;
    String target;
    int k;
    public StreamingGrep(int k, String target) {
        this.k = k;
        curId = utilId = lastId = -1;
        this.buffer = new ArrayDeque<>();
        this.output = new ArrayList<>();
        this.target = target;
    }

    public void process(String line) {
        curId++;
        if (line.contains(target)) {
            int id = curId - buffer.size();
            while (id > lastId && !buffer.isEmpty()) {
                output.add(buffer.pollFirst());
                lastId = id++;
            }

            output.add(line);
            buffer.clear();
            lastId = curId;
            utilId = curId + k;
        } else if (curId <= utilId) {
            output.add(line);
            lastId = curId;
        } else {
            buffer.add(line);
            if (buffer.size() > k) buffer.pollFirst();
        }
    }

    public List<String> getOutput() {
        return output;
    }
}
