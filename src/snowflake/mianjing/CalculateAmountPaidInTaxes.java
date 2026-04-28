package snowflake.mianjing;
/*

You are provided with a 0-indexed 2D integer array called brackets. Each item in this list is a pair [upper_bound, tax_rate].
This represents a tax bracket where:

upper_bound is the highest income amount included in this bracket.
tax_rate is the percentage of tax charged for this bracket.
The list brackets is sorted by upper_bound from smallest to largest. All upper bounds are distinct.

You are also given an integer income. You need to calculate the total tax based on a progressive tax system.

How Progressive Tax Works: Tax is calculated in steps or "chunks":

Money up to the first bracket's limit is taxed at the first rate.
Any money above the first limit—but below the second limit—is taxed at the second rate.
This pattern continues for each subsequent bracket until all your income is accounted for.
Your goal is to return the total amount of tax paid.

Sample Cases
Case 1:

Input: brackets = [[3,50],[7,10],[12,25]], income = 10

Output: 2.65

Case 2:

Input: brackets = [[1,0],[4,25],[5,50]], income = 2

Output: 0.25

Input Limits
1 <= brackets.length <= 100
1 <= upper_bound <= 1000
0 <= tax_rate <= 100
0 <= income <= 1000
upper_bound increases strictly (the list is sorted and values are unique).
 */

public class CalculateAmountPaidInTaxes {
    public double calculateTax(int[][] brackets, int income) {
        if (income == 0) {
            return 0;
        }
        double result = 0;
        int prev = 0;
        for (int i = 0; i < brackets.length; i++) {
            int bound = brackets[i][0] - prev;
            double rate = brackets[i][1] * 0.01;
            if (income <= bound) {
                result += income * rate;
                break;
            } else {
                income -= bound;
                result += bound * rate;
            }
            prev = brackets[i][0];
        }

        return result;
    }
}
