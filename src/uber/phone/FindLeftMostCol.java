package uber.phone;

public class FindLeftMostCol {
    public int findLeftMost(int[][] board) {
        int rows = board.length, cols = board[0].length;
        int max = cols - 1;
        for (int[] row : board) {
            max = Math.min(bsLeftRow(row), max);
        }
        return max;
    }

    private int bsLeftRow(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == 1) {
                r = mid;
            } else {
                l = mid;
            }
        }
        if (nums[l] == 1) {
            return l;
        }
        return r;
    }

//    int globalMax = Integer.MAX_VALUE;
//    int rows, cols;
//    int[][] board;
//    Queue<Integer> uncheckRows;
//    int threads;
//
//    public FindLeftMostCol(int[][] board) {
//        rows = board.length;
//        cols = board[0].length;
//        this.board = board;
//        this.uncheckRows = new LinkedList<>();
//        threads = 3;
//        for (int i = 0; i < rows; i++) {
//            uncheckRows.add(i);
//        }
//    }
//
//    class MyThread implements Runnable {
//        Thread guruthread;
//        int[] array;
//        int max;
//        public MyThread(int[] array, int max) {
//            this.array = array;
//            this.max = max;
//        }
//
//        @Override
//        public void run() {
//            System.out.println("Thread running" );
//            max = Math.min(max, bsLeftRow(array));
//        }
//
//        public void start() {
//            System.out.println("Thread started");
//            if (guruthread == null) {
//                guruthread = new Thread(this);
//                guruthread.start();
//            }
//        }
//
//        private int bsLeftRow(int[] nums) {
//            int l = 0, r = nums.length - 1;
//            while (l + 1 < r) {
//                int mid = l + (r - l) / 2;
//                if (nums[mid] == 1) {
//                    r = mid;
//                } else {
//                    l = mid;
//                }
//            }
//            if (nums[l] == 1) {
//                return l;
//            }
//            return r;
//        }
//    }
//    public synchronized int getRow() {
//        return uncheckRows.poll();
//    }
//
//    public int findLeftMostMultiThread() {
//        for (int i = 0; i < threads; i++) {
//            int row = getRow();
//            int[] array = board[row];
//            MyThread thread = new MyThread(array, globalMax);
//            thread.start();
//        }
//        return globalMax;
//    }


}
