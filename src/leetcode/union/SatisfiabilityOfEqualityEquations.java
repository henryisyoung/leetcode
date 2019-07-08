package leetcode.union;

import java.util.Arrays;
import java.util.Comparator;

public class SatisfiabilityOfEqualityEquations {
    public class Union {
        public int cap;
        public int[] size, father;

        public Union(int capacity) {
            this.cap = capacity;
            size = new int[capacity];
            father = new int[capacity];
            for (int i = 0; i < capacity; i++) {
                father[i] = i;
                size[i] = 1;
            }
        }

        public int find(int index) {
            while (index != father[index]) {
                index = father[index];
            }
            return index;
        }

        public void union(int a, int b) {
            int aFather = find(a);
            int bFather = find(b);
            if (aFather == bFather) {
                return;
            }
            if (size[aFather] > size[bFather]) {
                father[bFather] = aFather;
                size[aFather] += size[bFather];
            } else {
                father[aFather] = bFather;
                size[bFather] += size[aFather];
            }
        }
    }

    public boolean equationsPossible(String[] equations) {
        if (equations == null || equations.length == 0) return true;
        Union union = new Union(26);
        for (String eq : equations) {
            if (eq.length() != 4) return false;
            String sym = eq.substring(1, 3);
            int a = eq.charAt(0) - 'a', b = eq.charAt(3) - 'a';
            if (sym.equals("==")) {
                union.union(a, b);
            }
        }
        for (String eq : equations) {
            String sym = eq.substring(1, 3);
            if (sym.equals("!=")) {
                int a = eq.charAt(0) - 'a', b = eq.charAt(3) - 'a';
                int fa = union.find(a), fb = union.find(b);
                if (fa == fb) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String[] eq = {"c==c","x!=z","b==d"};
        String[] eq2 = {"a==b","b!=a"};
        SatisfiabilityOfEqualityEquations solver = new SatisfiabilityOfEqualityEquations();
        System.out.println(solver.equationsPossible(eq));
    }
}
