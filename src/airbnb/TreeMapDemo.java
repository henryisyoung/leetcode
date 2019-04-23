package airbnb;

import java.util.TreeMap;

public class TreeMapDemo {
    public static void main(String[] args)
    {

        // Creating an empty TreeMap
        TreeMap<Integer, String> tree_map = new TreeMap<>();

        // Mapping string values to int keys
        tree_map.put(10, "Geeks");
        tree_map.put(15, "4");
        tree_map.put(20, "Geeks");
        tree_map.put(25, "Welcomes");
        tree_map.put(30, "You");

        // Displaying the TreeMap
        System.out.println("The original map is: "
                + tree_map);

        // Displaying the submap
        System.out.println("The subMap is " + tree_map.subMap(15, false, 30, false));
    }

}
