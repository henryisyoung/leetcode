package snap;
//https://www.1point3acres.com/bbs/thread-540236-1-1.html

public class Operations {
    public boolean operations(int target, int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        return dfs(target, nums, 0, 0, 0);
    }

    private boolean dfs(int target, int[] nums, int sum, int lastOp, int idx) {
        if (sum == target) {
            return true;
        }
        boolean flag = false;
        for (int i = idx; i < nums.length; i++) {
            if (idx == 0) {
                return dfs(target, nums, sum, lastOp, i + 1);
            } else {
                // +
                boolean plus = dfs(target, nums, sum + nums[i], nums[i], i + 1);
                // -
                boolean minus = dfs(target, nums, sum - nums[i], -nums[i], i + 1);
                // *
                boolean multi = dfs(target, nums, sum - lastOp + lastOp * nums[i], lastOp * nums[i], i + 1);
                // /
                boolean divide = dfs(target, nums, sum - lastOp + lastOp / nums[i], lastOp / nums[i], i + 1);
                // * with （）
                boolean withbracket = dfs(target, nums, sum + lastOp * nums[i], lastOp * nums[i], i + 1);
                boolean withbracketDiv = dfs(target, nums, sum + lastOp / nums[i], lastOp / nums[i], i + 1);
                flag = plus | minus | divide | withbracket | withbracketDiv | multi;

            }
        }
        return flag;
    }
}
