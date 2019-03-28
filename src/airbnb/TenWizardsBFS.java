package airbnb;

import java.util.*;

public class TenWizardsBFS {
    public class Wizard{
        int idx, dis, fromId;
        public Wizard(int idx, int dis, int fromId){
            this.idx = idx;
            this.dis = dis;
            this.fromId = fromId;
        }
    }

    public int cost(List<List<Integer>> costList){
        int n = costList.size();
        Wizard[] wizards = new Wizard[n];
        for(int i = 0; i < n; i++) {
            wizards[i] = new Wizard(i, i == 0 ? 0 : Integer.MAX_VALUE, 0);
        }
        Queue<Wizard> queue = new LinkedList<>();
        queue.offer(wizards[0]);

        while(!queue.isEmpty()){
            Wizard w = queue.poll();
            for(int i: costList.get(w.idx)){
                int newDis = w.dis + (w.idx - i) * (w.idx - i);
                if(wizards[i].dis > newDis){
                    wizards[i].dis = newDis;
                    wizards[i].fromId = w.idx;
                    queue.offer(wizards[i]);
                }
            }
        }
        System.out.println("path " + buildPath(wizards, n));
        return wizards[n-1].dis;
    }

    private List<Integer> buildPath(Wizard[] wizards, int n) {
        int start = n - 1;
        List<Integer> path = new ArrayList<>();
        while (start != 0) {
            path.add(start);
            start = wizards[start].fromId;
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        List<List<Integer>> wizards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Integer> list = new ArrayList<>();
            if (i == 0) {
                list.add(1);
                list.add(2);
                list.add(3);
            } else if (i == 1) {
                list.add(2);
                list.add(3);
            } else if (i == 2) {
                list.add(3);
                list.add(4);
            } else if (i == 3) {
                list.add(4);
            }
            wizards.add(list);
        }
        TenWizardsBFS solver = new TenWizardsBFS();
        System.out.println("cost " + solver.cost(wizards));
    }
}
