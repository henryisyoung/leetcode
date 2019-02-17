package airbnb;

import java.util.*;

public class Pagination {
    public List<String> displayPages(List<String> input, int pageSize) {
        List<String> result = new ArrayList<>();
        if (input == null || input.size() == 0 || pageSize == 0) {
            return result;
        }
        Iterator<String> iter = input.iterator();
        List<String> isVisited = new ArrayList<>();
        boolean end = false;
        while (iter.hasNext()) {
            String cur = iter.next();
            String hostID = cur.split(",")[0];
            if (!isVisited.contains(hostID) || end) {
                result.add(cur);
                isVisited.add(hostID);
                iter.remove();
            }
            if (isVisited.size() == pageSize) {
                isVisited.clear();
                end = false;
                if (!input.isEmpty()) {
                    result.add(" ");
                }
                iter = input.iterator();
            }
            if (!iter.hasNext()) {
                iter = input.iterator();
                end = true;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Pagination pg = new Pagination();
        List<String> input = new ArrayList<>(Arrays.asList("1,sf","3,gf","3,hj","2,sd","1,yt","5,ty","3,ty","2,ds","3,as","6,uy"));
        List<String> list = pg.displayPages(input, 3);
        System.out.println(list.toString()); // [1,sf, 3,gf, 2,sd,  , 3,hj, 1,yt, 5,ty,  , 3,ty, 2,ds, 6,uy,  , 3,as]
    }
}
