package pinterest;

import java.util.*;

public class ReadNCharactersGivenRead4II {
    Queue<Character> remain = new LinkedList<>();

    public int read(char[] buf, int n) {
        int i = 0;
        // 队列不为空时，先读取队列中的暂存字符
        while(i < n && !remain.isEmpty()){
            buf[i] = remain.poll();
            i++;
        }
        char[] tmp = new char[4];
        for(; i < n; i += 4){

            int len = read4(tmp);
            // 如果读到字符多于我们需要的字符，需要暂存这些多余字符
            if(len > n - i){
                System.arraycopy(tmp, 0, buf, i, n - i);
                // 把多余的字符存入队列中
                for(int j = n - i; j < len; j++){
                    remain.offer(tmp[j]);
                }
                // 如果读到的字符少于我们需要的字符，直接拷贝
            } else {
                System.arraycopy(tmp, 0, buf, i, len);
            }
            // 同样的，如果读不满4个，说明数据已经读完，返回总所需长度和目前已经读到的长度的较小的
            if(len < 4) return Math.min(i + len, n);
        }
        // 如果到这里，说明都是完美读取，直接返回n
        return n;
    }

    private int read4(char[] buffer) {
        return 0;
    }
}
