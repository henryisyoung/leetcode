package google.vo.mianjing;

import java.util.PriorityQueue;

// https://leetcode.com/discuss/interview-question/1895536/google-phone-interview-time-to-wait-for-agent
public class TimeWaitAgent {
    /*
    *Jessica wants to get auto insurance. When arriving at the insurance company, there are N agents (number 1 to N) serving nobody, and M people have already arrived with the same demand.
    *The company follows the rule of first arrived first served, and if more than 2 agents can serve a customer at the same time, the customer will always choose the one with the smallest number.
    *For agents, each of them has a constant serving time that the ith agent will take T[i] minutes to serve a customer. Assume Jessica arrived at time 0, and all the agents are idle and start to serve the customers.
    *The question is how many minutes will Jessica needs to wait before meeting with an agent?
    https://leetcode.com/discuss/interview-question/1895536/google-phone-interview-time-to-wait-for-agent
     */

    public int calcServeTime(int[] agents, int m) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
           if(a[0] != b[0]) {
               return a[0] - b[0];
           }
           return a[1] - b[1];
        });
        for (int i = 0; i < agents.length; i++) {
            pq.add(new int[]{0, i});
        }
        for (int i = 0; i < m; i++) {
            int[] agent = pq.poll();
            int id = agent[1];
            int time = agent[0] + agents[id];
            pq.add(new int[]{time, id});
        }
        return pq.peek()[0];
    }
}
