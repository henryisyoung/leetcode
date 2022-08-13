package leetcode.union;

public class Union {
    public int cap;
    public int[] size, father;
    public int count;

    public Union(int capacity) {
        this.cap = capacity;
        size = new int[capacity];
        father = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            father[i] = i;
            size[i] = 1;

        }
        this.count = capacity;
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
        count--;
        if (size[aFather] > size[bFather]) {
            father[bFather] = aFather;
            size[aFather] += size[bFather];
        } else {
            father[aFather] = bFather;
            size[bFather] += size[aFather];
        }
    }
}
