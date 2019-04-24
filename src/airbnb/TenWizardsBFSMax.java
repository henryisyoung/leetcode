package airbnb;

import java.util.*;

public class TenWizardsBFSMax {
    public int cost(List<List<Integer>> costList){
       int n = costList.size();
       int target = n - 1;
       Wizard[] wizards = new Wizard[n];
       for (int i = 0; i < n; i++) {
           wizards[i] = new Wizard(0, i, i == 0 ? 0 : Integer.MIN_VALUE);
       }
       Queue<Wizard> queue = new LinkedList<>();
       queue.add(wizards[0]);

       while (!queue.isEmpty()) {
           Wizard cur = queue.poll();
           for (int next : costList.get(cur.wizard)) {
               int nextCost = cur.cost + (next - cur.wizard) * (next - cur.wizard);
               if (nextCost > wizards[next].cost) {
                   wizards[next].cost = nextCost;
                   wizards[next].fromWizard = cur.wizard;
                   queue.add(wizards[next]);
               }
           }
       }
       List<Integer> list = new ArrayList<>();
        while (target != 0) {
           list.add(target);
           target = wizards[target].fromWizard;
        }
        list.add(target);
        Collections.reverse(list);
        System.out.println(list);
       return wizards[n - 1].cost;
    }

    private class Wizard{
        int fromWizard, wizard, cost;
        public Wizard(int fromWizard, int wizard, int cost) {
            this.cost = cost;
            this.fromWizard = fromWizard;
            this.wizard = wizard;
        }
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
        TenWizardsBFSMax solver = new TenWizardsBFSMax();
        System.out.println("cost " + solver.cost(wizards));
    }
}
