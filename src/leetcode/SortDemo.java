package leetcode;

/*****************************************************************************
 *    This demonstrates soring in Java
 *
 *****************************************************************************/

import java.util.*;

public class SortDemo
{
    //@SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        //sort an array of strings using natural ordering
        String[] words1 = {"Fred","bob","Tom","Mark","john","Steve"};
        Arrays.sort(words1);
        System.out.println(Arrays.toString(words1));

        String[] words2 = {"Fred","bob","Tom","Mark","john","Steve"};
        Arrays.sort(words2, new Comp1());
        System.out.println(Arrays.toString(words2));

        //sort in reverse order
        String[] words3 = {"Fred","Bob","Tom","Mark","John","Steve"};
        Arrays.sort(words3, new Comp2());
        System.out.println(Arrays.toString(words3));

        //sort by emails
        String[] words4 = {"Fred@cmu.edu","Bob@yahoo.com",
                "Tom@gmail.com","Mark@ucla.edu","John@pit.edu","Steve@microsoft.com"};
        Arrays.sort(words4, new Comp3());
        System.out.println(Arrays.toString(words4));
    }
}

class Comp1 implements Comparator<String>
{
    public int compare(String obj1, String obj2)
    {
        return obj1.compareToIgnoreCase(obj2);
    }
}

class Comp2 implements Comparator<String>
{
    public int compare(String obj1, String obj2)
    {
        return obj2.compareTo(obj1);
    }
}

class Comp3 implements Comparator<String>
{
    public int compare(String obj1, String obj2)
    {
        String str1 = obj1.substring( obj1.indexOf("@") );
        String str2 = obj2.substring( obj2.indexOf("@") );
        return str1.compareToIgnoreCase(str2);
    }
}
