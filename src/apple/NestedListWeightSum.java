package apple;

import leetcode.solution.NestedInteger;

import java.util.List;

public class NestedListWeightSum {
    public int depthSum(List<NestedInteger> nestedList) {
        if(nestedList == null || nestedList.size() == 0) {
            return 0;
        }
        return depthSumHelper(nestedList, 1);
    }

    private int depthSumHelper(List<NestedInteger> nestedList, int lvl) {
        if(nestedList == null || nestedList.size() == 0) {
            return 0;
        }
        int sum = 0;
        for(NestedInteger ni : nestedList) {
            if(ni.isInteger()) {
                sum += lvl * ni.getInteger();
            } else {
                sum += depthSumHelper(ni.getList(), lvl + 1);
            }
        }
        return sum;
    }
}
