package uber.VO;

public class TripsCount {
    /*
    https://www.1point3acres.com/bbs/thread-911279-1-1.html
    好像没在刷题网找到。大意是这样的，输入 = [("jack", city1,ts1), ("mary", city1, ts2), ("jack", city2, ts2), ("mary", city2, ts3), ("jack", city3, ts3), ("jack", city2, ts4)， ("mary", city3, ts8)]
那么对于jack, 他的完整行程是 city1 -> city2 -> city3 -> city2，每个行程用相连的三个城市定义，所以Jack有两段行程 (city1, city2, city3), (city2, city3, city2)。类似的，mary有一段行程, (city1, city2, city3)
希望的输出是所有的行程 并按照出现次数从高到低输出。所以这里的输出是  (city1, city2, city3): 2,   (city2, city3, city2): 1
     */
}
