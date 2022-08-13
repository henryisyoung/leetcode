package google.vo.mianjing;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RestaurantWaitList {
    // https://leetcode.com/discuss/interview-question/1779091/google-phone-interview-usa-&#8205;&#8205;&#8204;&#8204;&#8204;&#8204;&#8205;&#8205;&#8204;&#8205;&#8204;&#8205;&#8205;&#8205;&#8204;&#8204;&#8204;&#8205;restaurant-wait-list
    /*
    Design a wait list for customers at restaurant:

    Add customers to wait list (for example: Bob, party of 4 people)
    Remove a customer from wait list
    Given a open table with N seats, remove and return the first customer party of size N
    Clarifications:

    10 unique table sizes
    Customer names unique
    FIFO if two parties have the same number of people
    Table with N seats must have exactly N people
    Ideal solution O(1) runtime for all 3 methods
     */

    int n;
    Map<Integer, Set<String>> waitList;

    public RestaurantWaitList(int n) {
        this.n = n;
        for (int i = 1; i <= n; i++) {
            waitList.put(i, new LinkedHashSet<>());
        }
    }

    public void AddCustomer(int size, String name) {
        String customer = size + "- " + name;
        waitList.get(size).add(customer);
    }

    public boolean removeCustomer(int size, String name) {
        String customer = size + "- " + name;
        if (waitList.get(size).contains(customer)) {
            waitList.get(size).remove(customer);
            return true;
        }
        return false;
    }

    public String getCustomer(int size) {
        Set<String> customers = waitList.get(size);
        if (customers.isEmpty()) {
            return "";
        }
        Iterator<String> iterator = customers.iterator();
        String customer = iterator.next();
        iterator.remove();
        return customer;
    }
}
