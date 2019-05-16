package twitter;

public class UserActivityMinutes {
//    第一道：给一堆用户以及其活跃时间的tuple list [<userID1, activeTime1>.....<userIDn,activeTimen>], 其中用户可以活跃于多个
//    timestamp，在同一分钟内（e.g. 10：01：01 and 10：01：59）活跃多次的只记为一个active minute；并且activeTime格式整数，
//    记录了从1975.1.1到当时的毫秒数。问题是让统计不同cumulated number of active minutes 对应的用户数量
//    （e.g. 总共活跃了x分钟的用户有多少个）解法：用hashmap和set解了一波，写完程序还要运行一下。面试官follow up，问这个东西用
//    mapreduce怎么解决。楼主有五六年没用过map reduce，凭印象讲了一下MapReduce的原理和怎么apply在这个问题上，面试官说差不多答对70%，
//    不过念在我不咋用的份上就不深究了。。。
//    第二道：给一个图和source以及target找source到target的最短路径
//    写完程序也要拿test case运行一下出正确结果才行
}
