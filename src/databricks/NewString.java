package databricks;

import java.util.*;

public class NewString {
    /*
    https://www.1point3acres.com/bbs/forum.php?mod=redirect&goto=findpost&ptid=743471&pid=15348684
     */

    class Block {
        List<Character> data;
        public Block() {
            this.data = new ArrayList<>();
        }
    }

    List<Block> blockList;
    int totalSize, blockSize;

    public NewString() {
        this.blockList = new ArrayList<>();
        this.blockSize = 0;
        this.totalSize = 0;
    }

    public Character read(int pos) {
        int[] array = find(pos);
        int index = array[0];
        pos = array[1];
        if (pos == -1) {
            return null;
        }
        Block block = blockList.get(index);
        return block.data.get(pos);
    }

    public void delete(int pos){
        int[] array = find(pos);
        int index = array[0];
        pos = array[1];
        if (pos == -1) {
            return ;
        }
        Block block = blockList.get(index);
        totalSize--;
        blockSize = (int) Math.sqrt(totalSize);
        block.data.remove(pos);
        maintain();
    }

    public void insert(char c, int pos){
//        totalSize++;
//        blockSize = (int) Math.sqrt(totalSize);
//
//        if (blockList.size() == 0) {
//            Block block = new Block();
//            block.data.add(c);
//            blockList.add(block);
//        } else {
//            int[] nums = find(pos);
//            Block block = blockList.get(nums[0]);
//            pos = nums[1];
//            if (pos == -1) {
//                // add to the last
//                block.data.add(c);
//            } else {
//                block.data.add(pos, c);
//            }
//        }
//
//        maintain();
        totalSize++;
        blockSize = (int) Math.sqrt(totalSize);
        if (blockList.size() == 0) {
            Block block = new Block();
            block.data.add(c);
            blockList.add(block);
        } else {

            int[] val = find(pos);
            int index = val[0];
            pos = val[1];
            Block block = blockList.get(index);
            if (pos == -1) {
                block.data.add(c);
            } else {
                block.data.add(pos, c);
            }
        }
        maintain();
    }

    private void maintain() {
        // split bigger
//        for (int i = 0; i < blockList.size(); i++) {
//            Block block = blockList.get(i);
//            if (block.data.size() > 2 * blockSize) {
//                Block splitB = new Block();
//                List<Character> list = block.data;
//                splitB.data = new ArrayList<>(list.subList(0, blockSize));
//                block.data = new ArrayList<>(list.subList(blockSize, list.size()));
//                blockList.add(i, splitB);
//            }
//        }
//
//        // merge smaller
//        for (int i = 0; i < blockList.size() - 1; i++) {
//            Block next = blockList.get(i + 1);
//            Block cur = blockList.get(i);
//            if (cur.data.size() + next.data.size() < blockSize) {
//                cur.data.addAll(next.data);
//                blockList.remove(i + 1);
//            }
//        }

        for (int i = 0; i < blockList.size(); i++) {
            Block curBlock = blockList.get(i);
            if (curBlock.data.size() > 2 * blockSize) {
                Block newBlock = new Block();
                List<Character> list = curBlock.data;
                newBlock.data = new ArrayList<>(list.subList(0, blockSize));
                curBlock.data = new ArrayList<>(list.subList(blockSize, list.size()));
                blockList.add(i, newBlock);
            }
        }

        // merge
        for (int i = 0; i < blockList.size() - 1; i++) {
            Block curBlock = blockList.get(i);
            Block nextBlock = blockList.get(i + 1);
            if (curBlock.data.size() + nextBlock.data.size() < blockSize) {
                curBlock.data.addAll(nextBlock.data);
                blockList.remove(i + 1);
            }
        }
    }

    // 0 is the index in blockList
    // 1 is the rest of pos, -1 means that is the very last one
    private int[] find(int pos) {
//        int sum = 0;
//        for (int i = 0; i < blockList.size(); i++) {
//            sum += blockList.get(i).data.size();
//            if (sum > pos) {
//                pos -= sum - blockList.get(i).data.size();
//                return new int[]{i, pos};
//            }
//        }
//        return new int[]{blockList.size() - 1, -1};
        for (int i = 0; i < blockList.size(); i++) {
            int curSize = blockList.get(i).data.size();
            if (curSize > pos) {
                return new int[]{i, pos};
            } else {
                pos -= curSize;
            }
        }
        return new int[]{blockList.size() - 1, -1};
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block block : blockList) {
            for (char c : block.data) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        NewString bl = new NewString();
        bl.insert('a', 10);
        bl.insert('b', 10);
        bl.insert('c', 10);
        bl.insert('d', 10);
        bl.insert('e', 1);
        bl.insert('k', 1);
        bl.insert('t', 1);
        bl.insert('m', 1);
        bl.insert('n', 1);
        System.out.println(bl.toString());
        System.out.println(bl.read(7));
        bl.delete(7);
        System.out.println(bl.toString()); //anmtkebd
        bl.delete(7);                 //anmtkeb
        bl.delete(4);                 // anmteb
        bl.delete(4);                 // anmtb
        System.out.println(bl.toString()); // anmtb
        bl.delete(3);                   // anmb
        System.out.println(bl.toString());
    }
}
