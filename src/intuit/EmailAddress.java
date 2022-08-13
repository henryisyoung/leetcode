package intuit;

import java.util.HashSet;
import java.util.Set;

public class EmailAddress {
    public static int numUniqueEmails(String[] emails) {
        Set<String> set = new HashSet<>();
        for(String email : emails) {
            String[] array = email.split("@");
            String localName = normalizeLocalName(array[0]);
            System.out.println(localName);
            set.add(localName + "@" + array[1]);
        }

        return set.size();
    }

    private static String normalizeLocalName(String str) {
        str = str.replace(".", "");
        System.out.println(str);
        int index = str.indexOf("+");
        if(index != -1) {
            str = str.substring(0, index);
        }
        return str;
    }

    public static void main(String[] args) {
        String[] emails  = {"test.email+alex@leetcode.com","test.email.leet+alex@code.com"};
        System.out.println(numUniqueEmails(emails));

        System.out.println("asd.asd.asd".replace(".", ""));
    }

}
