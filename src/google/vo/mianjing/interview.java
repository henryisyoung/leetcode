//1.
// 1 刷题网： 一流衣领 1010 https://leetcode.com/problems/pairs-of-songs-with-total-durations-divisible-by-60/
//系统设计 *1
//rate limiter ， 算法这边我觉得说出常用的window， sliding window， bucket 基本就差不多了。然后还是聊架构。算是比较常规吧，分享几个我觉得比较值得注意的点：
//1 - bucket怎么refill，什么时候refill，为什么。如果是多个server host 怎么refill， server之间怎么交流。不同的data center又要如何处理？ server之间的交流频率又是什么。为什么。
//2 - 事先的clarify，题目就一行。所以先聊requirement，再说细节。 我问问题的时候，对面就开始满意笑了。尤其问到distributed system，对面就很开心明显。我至少问了10个问题，面试官都记录在doc上了。
//3 - 每一个系统的细节。比如load balancer，为啥要有LB，会不会是single point of failure，如果是的话，怎么解决。（其他layer类似的问题都会过一遍）
//输入是一副扑克牌（不一定是完整一副‍‌，就是一个list of 牌），然后找最大的full flush （同花顺）
//follow up是各种优化和假设牌不只是1-13， 可能是1-n。 同理 花色不一定是4个可能是0-n种。
//// https://www.1point3acres.com/bbs/thread-889010-1-1.html

//2.
//第一轮coding
//Rate limiter
//第二轮 System design
//设计一个rate limiter （没错，和第一题一样，只是是系统设计题）
//DAU1B, QPS 1M, Latency < 1ms
//第三轮 BQ
//很专业的深挖如何priority事情和处理不同意见
//第四轮coding
//matrix最大路径变种加难版本。
//第五轮 System design
//题目很新奇，在GCP已有object系统基础上（增删改查），实现object 的compose。
//功能和这个类似：https://cloud.google.com/storage/docs/gsutil/commands/compose
//我问考官具体的use case，考官说不用考虑use case，可以抽象成各种类型文件content bit连起来就好。
//我之前没有搞过object storage，只了解一些基本知识。
//所以提了俩方案：
//1. deep copy文件然后compose，更新metadata DB。write slow， read quick
//2. 创建一个索引表，用户读取的时候按照索引iterate 提取子object，可能出现多层嵌套。write quick， read slow
//为了优化上面方案这个，参考了WAL逻辑，先create metadata更新+索引，之后worker在去post copy合并，这样如果用户不是立刻读取，那么write quick，read quick。
//我也提出讲了object 文件用chunk存储的，也可以考虑chunk层做索引。
//我只收集了functional requirement。 no-functional requirement, estimate, api, Data 都不需要我说，全程问我算法怎么设计，压缩or加密文件怎么处理。这题答到最后不知道考官想问什么。
//// https://www.1point3acres.com/bbs/thread-888984-1-1.html

//3.
//1. 设计一个温度tracker，只保留24小时温度。需要加温度和查询最大问题
//3. 给一个无向图，给一个wrong path among different city，然后想办法用最少的change去update 这个wrong path里面不正确的city名字，使得它valid。
//4. 面经见过却没写过的题目。
//参考这个链接第一个题目
//https://www.1point3acres.com/bbs/thread-873959-1-1.html。
//安卓version merge。其实题目不是很难，但edge case要小心考虑，或者说用不同的想法提前处理好edge case这样的话才能更好的做这个题‌目，否则后面的code 处理edge case会花掉很多时间
//// https://www.1point3acres.com/bbs/thread-888955-1-1.html

//4.
//题目是离口屋留变了一下，给一个排过序的intervals和一个单独的interval，把第二个merge进去返回排过序的list
//// https://www.1point3acres.com/bbs/thread-888770-1-1.html
//"""
//public class Solution {
//    public List<Interval> insert(List<Interval> intervals, Interval newInterval) {
//        if (intervals.size() == 0) {
//            intervals.add(newInterval);
//            return intervals;
//        }
//        List<Interval> result = new ArrayList<>();
//        int poi = findPosition(intervals, newInterval);
//        int start = intervals.get(0).start;
//        int end = intervals.get(0).end;
//        for (int i = 0; i < intervals.size(); i++) {
//            if (end >= intervals.get(i).start) {
//                end = Math.max(end, intervals.get(i).end);
//            }
//            else {
//                result.add(new Interval(start, end));
//                start = intervals.get(i).start;
//                end = intervals.get(i).end;
//            }
//        }
//        result.add(new Interval(start, end));
//        return result;
//    }
//
//    private int findPosition(List<Interval> intervals, Interval newInterval) {
//        int left = 0;
//        int right = intervals.size() - 1;
//        while (left + 1 < right) {
//            int mid = left + (right - left) / 2;
//            if (intervals.get(mid).start >= newInterval.start) {
//                right = mid;
//            }
//            else {
//                left = mid;
//            }
//        }
//        if (intervals.get(right).start < newInterval.start) {
//            intervals.add(newInterval);
//            return right + 1;
//        }
//        else if (intervals.get(left).start >= newInterval.start) {
//            intervals.add(0, newInterval);
//            return 0;
//        }
//        else {
//            intervals.add(right, newInterval);
//            return right + 1;
//        }
//    }
//}
//"""

//5.
//第一题 cord tree. 题目满长的,得先理解cord tree是什么,接着四小题,包含怎么找第几个char, 怎么取某范围内的char, 怎么删除某些范围内的char.
//时间耗太久, 结果没写完。
//第二题,有几个city,有分别的fun values, 然后也有一个各别的班机资料,可以知道从哪个城市到哪个城市有班机,不限定起点,求走四个城市可以让fun Value最大.
//dfs, bfs 都可以解,但求优化解法,大家可以讨论下,我后来只有机会用口头讲一下优化解法。
//第三题, 在tree上要陆续删除leaf, 1)第一小题是问, 如果删除leaf后, 某些node变成leaf,这些node要立刻成为下个删除的目标, 2)第二小题是问, 这些node（刚变成leaf的）不能立刻成为删除的目标.
//// https://www.1point3acres.com/bbs/thread-888288-1-1.html

//6.
//第一轮： BQ，意大利总监，很happy，聊得很开心。
//第二轮：Coding，白人大姐，很欢乐。考了一道很简单的，利口 戚戚 简化版，只需要输出组合的个数。
//第三轮：SD，白人L6大哥。题目是，给全世界的用户在每天的localtime 8pm，个性化推送top 10新闻。正好遇上我的强项，面试官表示我说的方案和他们线上在用的基本一致。
//第四轮：Coding，白人L6小哥。位运算题。理解题意很费劲，大概花了15分钟才理解是啥意思。题不难做，感觉就是easy难度。
//第五轮：Coding，南亚大哥。问一个网络上，一个节点可以对其相邻节点广播，广播完他就关机了，然后问是否从J点出发能够到K点？非常典型的BFS题。follow-up是
//假设有多个点（>=2）同时往一个节点广播，这个点就失效了。本质上是在BFS维护一个map<Node, 步数>，如果发现有这样的对存在了，证明有另外一个点会向该点广播，直接返回零。
//// https://www.1point3acres.com/bbs/thread-888104-1-1.html

//7.
//面经一共6轮coding一轮bq我只分享一下VO的因为加面的没啥参考意义都是dfs
//1. coding 梨扣 气儿意 https://leetcode.com/problems/accounts-merge/
//2. coding 用anagram思想相关的题，不难，但是题目非常模糊需要大量的communication
//3. BQ
//4. coding weighted sampling，跟梨扣不完全一样但是基本套路差不多
//5. 两点之间求最短路径的问题。

//8.
//• BQ: 标准题. 最自豪的project, 需要改进学习的缺点
//• 衣舞芭  https://leetcode.com/problems/read-n-characters-given-read4-ii-call-multiple-times/
//• 参遛溜 https://leetcode.com/problems/find-leaves-of-binary-tree/
//• 面试官自己出的 Merge arrays 相关. 面试官是个老白人 连Java都不会写
//• System Design: Design an online turn-based game.
//给个思路, 先design online game system, including user service(using SQL due to Consistency over Availability),
//game matching service(with NoSQL due to Availability over Consistency),
//and game services(with Redis due to scale and super low latency. 使用Mast/slave redundancy to prevent Single Point of Failure).
//然后Game Service API设计再加入"turn-based"元素的APIs.
//Push server and Client选Web Sockets connection (v.s. Long Polling)
//// https://www.1point3acres.com/bbs/thread-890262-1-1.html

//9.
//面经最近经常出现的interval insert， 稍微有点不同就是他需要你输出每次插入一个区间之后的需要画的长度，
//比如一个stream[[4,10], [7, 13], [20, 30], [1, 40]] 那输出就分别是6， 3， 10， 20，
//因为一开始你画了4~10 长度是6， 然后7-13的时候因为7~10这一部分你不需要画了所以只有10~13所以长度为3 以此类推。。。 用treemap也行，每次去插入也行。
//我用treemap写最后写完有点没时间了，但我也不确定有没有bug free，dry run了两个输入就差的不多了，中间还改了一下 也不知道小姐姐怎么想
//
//第二轮体验最差，是一个OOD，白人长胡子大叔，直接做题，给了个目录结构，我一开始还以为要做压缩路径之类的结果不是
//比如 dir（id=1）- node1（id=2）-file1（id5）（100B）
// -file2（id6）（200B）
//-node2（id3） - file3（id7） 400B
// -file4（200B）
//然后说id5的size就是100B，node1因为包含了file1，file2所以id=2的时候size是300B。以此类推id=1的时候size就是900B，
//类似于叶子结点都是实际的file有实际的大小，而中间节点就是他的子节点的size的和。其实结构很简单，他就让实现这个东西，
//当给一个targetId要返回对应的size。但是On不行OlogN也不行，我看他的意思是要O1，大概是用map在构造的时候多花点时间吧。。。
//这轮最坑的就是跟他交流不畅 每次我问他的时候他都要沉默很久 跟他说话也得不到回应 总之就是很难受 就中间花了大量时间在想知道他到底要啥 然后他又拒绝交流
//我就写了一大堆但是也估计没啥用。。。这轮应该不太行
//
//第三轮 BQ 很短20分钟就结束了 有一个问题是关于你有没有遇到过 道德/伦理影响你做技术决定的时候。。。
//
//最简单的一轮 一个友善的印度大叔 一开始问了一个极其简单的BFS问题，岛的那道题变形，其实用DFS做也一样。就是click 一个点，如果是1就不变，如果是0就把周围所有0变成1，染色的这么一个概念。
//而且也没有follow up。直接出了 一个排序好的数组去重，要求On时间O1空间，双指针即可。唯一要注意的是他是用的Integer 数组不是int数组
//比较的时候以及有需要填入null的时候。反正非常简单，他自己应该开了一个编译器，每次我dry run完他都把code复制去他那里run了一遍，然后应该都通过了。
//
//第五轮是一个国人小姐姐，人非常开朗友善，还会给无声的提醒哈哈哈，因为她一开始给了三道热身题都是关于java pass in reference的然后有几个scena‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌rio
// 传来传去最后到底变成了啥。有一道题我有点拿不准，尝试的说了一个答案她就沉默了。。。然后我思考了一会说出了第二个答案她立刻说对对是这样的
// 然后把我的答案记录在了codepad上 人真好啊   
// 但是我因为第五轮真的是很晕 小姐姐甚至出了一道很简答的 卖股票问题 而且是贰那道题 就是过一遍低买高卖就行了 我居然一开始在那苦思冥想写了个n3的解法，
// 小姐姐估计都无语了 说那你可不可以写一个On时间O1空间的解法 我又想了一会才恍然大悟 小姐姐说那你快点写吧 写完我感觉真是羞愧啊 人家有意给你放水 结果抬都抬不动 真是气死人家了
//
//// https://www.1point3acres.com/bbs/thread-890245-1-1.html
//
// """
// class Solution {
//    // time complexity O(nlogn) -> each interval is at most insert/remove once
//    fun amountPainted(paint: Array<IntArray>): IntArray {
//        val intervals = TreeMap<Int, Int>()
//        val area = IntArray(paint.size) { 0 }
//
//        for (i in 0..paint.size - 1) {
//            val currentInterval = paint[i]
//            var start = currentInterval[0]
//            var end = currentInterval[1]
//            var curArea = end - start
//
//            val lessInterval = intervals.floorEntry(start)
//            if (lessInterval != null) {
//                if (lessInterval.value >= end) {
//                    continue
//                }
//
//                if (lessInterval.value >= start) {
//                    curArea = curArea - (lessInterval.value - start)
//                    start = lessInterval.key
//                    intervals.remove(lessInterval.key)
//                }
//            }
//
//            var nextInterval = intervals.ceilingEntry(start)
//            while (nextInterval != null) {
//                if (nextInterval.key > end) {
//                    break
//                }
//                curArea -= Math.min(end, nextInterval.value) - nextInterval.key
//                intervals.remove(nextInterval.key)
//                end = Math.max(end, nextInterval.value)
//                nextInterval = intervals.ceilingEntry(start)
//            }
//
//            area[i] = curArea
//            intervals[start] = end
//
//            // println(intervals)
//        }
//
//        return area
//    }
//}
// """

//
//10.
//第一轮 黑妹 给一组replacements<beginIndex,replaceString,replacedString> 和原字符串str，
//先判断原字符串str从beginIndex开始的substring为replacedString，如果存在，把它replace成replaceString。
//LC吧吧散。想问问下面这样的写法时间复杂度多大，不包括sorting?
//
//第二轮 有口音的白人 给一个dict，包含很多string，找出类似于这样的abc->ab->a 或者erfg->erg->rg->g这样的chain并且返回最大开始string例如这个例子就是erfg。
//follow up是万一删除某个word怎么样。好像是lc药零肆吧 变种 如果不是求更正
//
//第三轮 给一组painting的数据，第一个是start第二个是end【2，4】【3，5】【2，30】【30，40】返回一个数组记录每个paint的范围大小，前面paint过的不能paint，例如这个例子就是【2，2，25，10】。lc尔要吴吧。刷过一遍，面试没想起来，给了提示才想起用扫描数组的方式，
//没有skip painted area,依旧是暴力解。感觉就这轮比较拉胯。
//
//第四轮 被鸽了
//第五轮 给定一组request，形式是startTime,url,status code,duration，求每个时间节点（每个request的startTime）对应要处理的request
//10001 url1 404 10
//10002 url2 500 1
//10010 url3 300 10
//10009 url4 200 100
//返回10001->1 10002->2 10010->1 10009->2
//我用了类似range addition的思路 不过应该不是最优解，有人知道这道对应lc那道题吗
//后面两轮都问了最近的technical challenge，有一两轮都问了会用什么cases去做test，会怎么test。祈祷下周补的bq轮，攒人品，求米。
//
//第一轮是吧散散 打错
//
//// https://www.1point3acres.com/bbs/thread-890182-1-1.html

//
//11.
//1. 给一个文件系统，python的字典形式（面试官用的hashmap），里边的key有文件的类别（文件夹/文件）和大小，
//文件夹类型里有一个list里边存了属于它的文件/文件夹，文件直接给出size，文件夹没有，现在随意给出一个文件/文件夹号，求size。
//在一个类似google doc上写的，写完之后面试官让我过一遍自己的代码，然后我终于发现了错误，但解决方法不是最优，面试官提醒了下才写出来更好的方法。
//2. 说如果每次计算文件夹size都要重复所有的计算过程，比如文件夹1中包含文件夹2，但基于我写出来的代码，
//如果要分别知道文件夹1和‌文件夹2的大小，要计算一遍1，又要计算一遍2，问有没有什么优化。
//// https://www.1point3acres.com/bbs/thread-890077-1-1.html

//12.
//
//coding 1: 给一个多叉树的root, 让删除这个树。dfs bfs 遍历挨个删秒出，time 是O(N), memory O(logN), followup是怎么O(1) memory, 这可难住了，
//抓耳挠腮的边讨论边要hint，最后想了个flatten tree的做法，然而没写出来， 哎。。。 https://keithschwarz.com/interesting/code/?dir=inplace-tree-delete
//coding 2: 有一堆机器，一字排开，可以给左右两边的机器发消息，每个机器上有6个function,
//给了 hasLeft(), hasRight(), sendLeft(msg), sendRight(msg),
//让你implement receiveLeft(msg), receiveRight(msg), 和main(), 然后在main里面利用这6个函数，
//让每个机器print出来整个network里有多少机器。花了好长时间理解问题，最后好不容易磕磕绊绊的写完了，随便问了两个followup就到时间了。
//coding 3: 写一个Classifier class, 里面有三个function让你写，
//前两个简单，is_above_threshold(x, threshold), x >= threshold就返回1， 不然就返回-1，
//has_error(label, should_be_label), 如果label == should_be_label, 那就是对的，
//反之就是有error，然后input是一个x (list) 和 y (list), 让你写一个train(x, y), 返回threshold让他的error最少，举个例子，x=[1,2,3,4], y=[1, -1, 1, 1], train()出来的thresh‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌old 应该是1， 这样只有2是错的，error count是1。 让我先用brutal force做，很快写出来了，然后讨论怎么improve time complexity，边讨论边hint的后来出了个O(n)的方法。 基本不给思考时间，给了hint 3秒不接话他就又继续解释。Again, 满身大汗。
//SD： 设计slack，参考设计facebook messenger, 一个意思
//
//// https://www.1point3acres.com/bbs/thread-890058-1-1.html

//13.
//第一轮： word compress， 给一个array 然后在遇到一个符号，比如 255 的时候开始back reference，距离是255 后一位， 长度的255 后两位，
//大概是 easy to medium 难度 ？ 直接scan array 就好了 读到255 的同时 再check下接下来的2 位。
//然后follow up 就是 各种corner case， 为负数怎么办 ？ 如果我们也想要255 怎么办。 面试官人很好，这轮应该差不多过了。
//第二轮： 一个60 多白人老头 看着很技术狂。 一上来问了下 data schema 相关的 然后问下了 如果处理streaming data 用messaging queue。
//最后oop 问了 一个什么callback 的最大次数问题， 我clarify 了半天 才算理解 他想要干什么，但是code 基本也没写出来， 感觉 这轮 fail 掉了。
//第三轮： 应该是两个 国人小姐姐，问了个简单的graph 题， 然后bfs。 图不用自己建，可以直接用 api 接口。 秒了，
//然后follow up 是要define rule 看要不要mark down。 需要用到之前实现的code。 磕磕巴巴写出来了。
// 但fol‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌low up 一开始她是问 一个更难的的，然后我没理解 他让我开始写 这个简单的markdown。 反正code 是刚好写出来了，但是没有时间写她最开始问的那个问题了。
//
//
//第4轮： bq - 一大堆tell me about a time - 然后会不断follow up 直到问不下去。然后最后聊了下diversity这个topic。
//就临场发挥了 之前准备的bq 一共没用上。 也不知道面得怎么样，但跟面试官还算聊得来 最后ta 很开心 希望能给个过！
//第5轮：经典路由器题  medium难度， 需要自己build graph 然后过程中需要判断下 range 的范围。 最后bfs traversal。
//
//// https://www.1point3acres.com/bbs/thread-889995-1-1.html
//
//14.
//第1题. 给了Queue Interface, 有peek（）， poll（）和isEmpty（）这三个function，
//并且假设我们这里poll是非常expensive的。现在input是Queue[ ], 要求return最短的那个Queue的length。follow up 是求和最小的queue的sum（queue里没有负数）
//第2题. 蠡口散刘柳，写完recursion写iteration
//第3题. 写一个class，要求时间复杂度O(1) 实现
//1. ack(int n), 可以不断ack已经收到的number;  
//2. 可以getH, 可以return出来第一个从1开始的连续ack的interval的右边界
//比如ack(1), ack(4)， 此时getH，需要return 1
//继续ack(2), 此时getH，需要return 2
//继续ack(5), 此时getH，需要return 2
//继续ack(3), 此时getH，需要return 5
//Follow up 3.1：ack过的那些数，memory可能存不下， 怎么优化我的代码
//Follow up 3.2：实现一个getUnack function，return目前没ack的东西
//Follow up 3.3：假如1-9被ack了，10没有被ack了，但是如果11-110（共100个）被ack了，那么我们就说，放弃10，此时再getH这试需要return110
//第4题. 实现一个英语西班牙语单词翻译机：1. addTranslation(English, hello, Spanish, hola)  2. getTranslation(Spanish, hola), re‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌turn hello
//Follow up: 实现一个多种语言单词翻译机（多种语言之间没有重复词汇，即hello只可能出现在英语里，不可能出现在其他语言里）:
//addTranslation(English, hello, Spanish, hola)
//addTranslation(Spanish, hola, French, bonjour)
//getTranslation(English, hello, French) --> return bonjour
//
//// https://www.1point3acres.com/bbs/thread-889876-1-1.html
//
//15.
//1. 人美心善的白人小哥哥，给一个path string比如说“asdfghjkloiujkjhsgfrert” 再给a list of 世界上所有的 English words (有1000k左右)，
//返回能被这个path cover的所有words. 一个word被path string cover的意思是 这个word是path的一部分但是不需要连续 比如说给的这个例子里的path string就可以cover ["ghost" , "alert" ...] 其中"ghost" 是因为 "asdfghjkloiujkjhsgfrert"; "alert"是因为 “asdfghjkloiujkjhsgfrert”。
//这一题LZ很快想到了思路，写到中间卡了，面试官问了个问题引导了一下 (对我来说是很critical的提示)， 最后回答了时间复杂度。
//2. 人美心善的棕人大叔，给一个array, in place删掉里面所有的'a'， 第二问是 删掉里面所有的'a' 同时如果遇到一个'b'就把这个 'b'替换成 'bee' 都是要in place 做。这一轮和大叔聊了好几种方法，实现了他想要的。
//3. 人美心善的国人小姐姐，给一堆circle，每个circle都有[x坐标, y坐标, radius] 如果两个circle重叠说明两个circle在一个group，
//然后如果a 和 b在一个group, b和c在一个group，我们就说a b c 都在一个group。
//第一问判断是不是所有的circle是一个group，写了code 口头跑了几个test cases。
//follow up是总共有多少个groups，很快说了想法based on第一问，小姐姐同意写了code，但是n ^ 3 time complexity, 最后几分钟问有没有更优解，没答上来.. （这轮感谢小姐姐没有给red flag，其实清醒点想想n ^ 2的方法挺直接的，只是LZ当时脑子里都是怎么优化第一问的想法）
//4. 人美心善的国人小哥哥，给一个word和a list of strings，判断是否match。 match条件是word的每一个char 都存在于list的一个string里 而且顺序不能错.
//比如 word = "cat", list = ["cathy" "caught" "a" "cat" "ye!"] 返回True。第二问是 如果word一个char连续mismatch了k个string 那就返回False。
//(这个题第一问脑子已经不转了 快快说出了一个recursive的方法 类似backtracking，小哥说 这是一个acceptable的solution 你可以写，写完了以后 他说很suprise 这个方法对第二问很有帮助，问我是咋想的，
//	(我也不知道我是咋想的 个人感觉backtracking是最不费脑子的一个算法lol) 然后clarify了follow up，写完了。
//	之后还有一段时间 又问了一下第一问有什么更简单的solution，说了BFS, dp 小哥都说太麻烦，往greedy上想。最后小哥说没关系，这不会‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌影响到你的面试结果。 很感谢小哥高抬贵手。
//感觉每个题单独拉出来还好，但连着说话说5个小时 还是很累的，尤其是边思考边说话更累了。所以LZ总结出来的经验是，有的时候面试官不都要求最完美的解，节省精力起见，一些常见的狗家思路: Trie, backtracking, dfs, bfs, dfs + bfs, topology sort ..  有什么思路就先说，沟通为主，有时候面试官表现出不太满意，赶紧换思路。目的是写出他想要的结果，在他的评分系统里拿最高分，一般面试官也会想办法帮助你的；不需要一上来就target on最优解，当然如果有最优解，快快说出来。然后google主要看算法所以别说废话，保存体力。(别的公司我觉得可以酌情扯扯淡啥的）
//
//// https://www.1point3acres.com/bbs/thread-889795-1-1.html
//
//16.
//第一轮
//设计个数据结构，给一堆 ip 网址的pair 例如(1.2.3.4, "googlevideo.com")，要求能查找 某个ip 访问过的所有网址
//如果每个访问有expiration time (1.2.3.4, "googlevideo.com"， 60) 表示60秒过期，要求查找返回的 网址是没过期的
//这一轮题目简单的有点不敢相信...
//第二轮
//有一台机器不停的在记录 当前温度，
//要求实现getMax 返回24小时内数据的最大温度
//第三轮
//有几个人初始在不同的building 里 例如 [1 , 2 , 5] 代表有三个人 分别在1 ，2 ，5 号楼
//然后是 餐厅的location 例如 [2, 4， 6] 代表2,4,6号楼是餐厅
//然后是 building 之间的edge 例如[[1,2], [3,4], [1,5], [5,6]] 代表1和2 3和4 1 和5 之间可以通行
//这几个人要约饭，找到一个餐厅 可以让这几个人最早时间见面
//这题在其他人面经里也见到过
//第四轮
//常规bq： 问了很多phd thesis的东西 比如most rewardable stuff in your thesis, 然后就是常规bq问题most challenging project， 面试官也是个转码PhD，也是搞基础物理搞得要吐了转码了, 纯聊天吐槽
//第五轮
//也是其他面经中出现过的
//设计个数据结构储存文件，要求能够根据id 返回文件夹或者文件的大小
//root (id=1)
//    dir (id=2)
//         file1 (i‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌d=4): 3Mb
//         file2 (id=5): 6Mb
//    file3 (id=3):  (1Mb)
//总体来说题目都不难，运气不错，不过题目基本都给的很模糊，很多东西需要跟面试官沟通去讨论。
//
//// https://www.1point3acres.com/bbs/thread-889719-1-1.html
//
//17.
//遇到了挺年轻的一个interviewer，好像是去年刚刚毕业的，小哥哥是native speaker，沟通没有什么问题，语气没什么起伏，但也挺友好。
//问了一点bq以后直入coding主题。一个挺直接的graph traversal. Graph is stored in dictionary with keys as nodes and values as neighboring nodes.
//Guaranteed to be a ring, return the path either counterclockwise or clockwise following the ring until you get all nodes in the ring.
//先给了O(n) time & O(n) space complexity。 小哥问能不能improve space complexity。给了一个hint后做出来了optimized version。
//因为是第一场面试，写code不是很流畅，有时候会有一两个bug，但是小哥提醒了以后我有处理好。我每次写完code都仔细run through test case，特别是edge cases。
//follow up is how to modify code to catch invalid inputs.
//这边感觉主要测你考虑edge case的能力，其中一个edge case我没想到是小哥直接提出来的，不过是个挺简单的edge case，立刻就implement了。
//
//// https://www.1point3acres.com/bbs/thread-889668-1-1.html

//
//18.
//第一轮clock advance，试了几个关键字没找到原题，
//实现一个Clock类的advance方法，从当前时间advance到目标时间且advance次数最少。
//提供的API是Advance 1hr 15mins 5mins 1min，用greedy可解
//follow up是假设advance的API也是input，可能有任意多个API去advance任意时间，也可能有重复，这种情况greedy就不行了，
//我举了个反例然后提了dp的方法，没有implement完
//第二轮unique subtree，有点类似舞龄拔的思路，就是key需要变成subtree的结构而不是sum，
//这个题我纠结了比较久怎么把tree和hashmap的key一一对应，
//一开始给了补全成complete binary tree的方法但是时间空间都爆炸，后来想到可以只写到每一个叶节点下面的两个null也是唯一的，
//说了思路但是没来得及证明为什么是一一对应
//第三轮几乎是system design题，输入是一串request（id timestamp）数据，问只想处理每个id的request一次该怎么做，显然用hashset即可做，
//接着问如果large scale 怎么办，我提到加expiration time，然后就是一些具体该怎么implement的问题，这一轮感觉被黑了，
//从一开始老印面试官就不是很热情，过程中各种反问或者沉默，tiyanhencha
//第四轮原题依旧伞骑，我还先表演了下n^3的dp然后说可以简化到n^2，
//这里演砸的一点是dp循环中最简单的方式就是存一个临时变量curMax，然后每一列变成curMax=Math.max(curMax-1, prevRow) ,
//但我表演的是每次比较下一列时和更新过的上一列比较curRo‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌w = Math.max(curRow[i-1], prevRow)，增加了一些没必要的edge cases。。follow up
//是如果每行不能在同一列怎么办，非常简单，改两行代码即可
//
//// https://www.1point3acres.com/bbs/thread-889599-1-1.html

//19.
//搜了一下名字发现是白人小哥, 上来简单聊了一下就开始做题了. 只有一题, scrabble board
//输入: matrix (字母和.表示的空), words=["HELLO", "EGG", "HI"]
//matrix 举例:
//. . . . . . . .
//. HELLO .
//. . E . . . . .
//. . G . . . HI
//判断所有的字(横排和竖排)是否在words里面, 以及是否所有的字母都相连: True/False. ‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌
//先确认一些条件, 比如4-连接等, 然后询问是否可以用两个函数分别处理两个判断(一开始以为要一遍实现两个判断), 得到肯定答复之后就比较简单了.
//中途讨论一些complexity之类的就结束主题了.
//
//// https://www.1point3acres.com/bbs/thread-889536-1-1.html


//20.
//第一轮Behavioral：how to resolve different opinion with Tech Lead and coworkers; how to share constructive feedback to coworkers; how to face personality conflict in the team; what to do if you are manager; how to face the situation when team spend a lot of time on unit tests, etc.
//第二轮Coding：find bad commits，假设给定n个commit id(从0 到 n-1）和一个
//	API worse_commit(commit_id1, commit_id2)。
//	这个API return True if commit_id1 导致performace regression，return False if the performance stays neutual。
//	问如何找到所有的bad commit ids。follow up是，如果只有k（k << n) 个bad commits，time complexity是多少。
//第三轮Coding：实现一个Tree class。要求support any number of children，并且每个node 的 depth 不能超过一个给定的值（max_depth). 要求实现一下methods：
//insert(parent_id, node_id) -> 添加一个新的node作为parent_id 的child
//raise exception if parent_id doesn't exist in the tree or node_id already exists
//raise expcetion if the new depth of the node exceeds max_depth
//move(sub_root_id, parent_id) -> 移动subtree 到新的parent下
//raise exception if sub_root_id or parent_id doesn't exist.
//raise exception if the move leads to a cycle
//raise exception if the new depth of any node under sub_root exceeds max_depth
//delete(node_id) -> 删除一个node
//raise exception if node_id doesn''t exist
//find(node_id) -> 返回True如果no‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌de在tree里，否则False
//所有这些method除了delete最好是O(max_depth)实现
//第四轮 SD：设计chrome的phishing detection功能，假设ML team可以提供所有phishing url。
//第五轮 Coding: 实现一个interface，支持两个function：
//addOrReplace(index, number) -> 在index的位置存放 number，如果index已有数据则覆盖原来的值
//findSmallestIndex(number) -> 返回存放number最小的index，如果number不存在则返回-1
//// https://www.1point3acres.com/bbs/thread-889521-1-1.html

//21.
//1. 利口儿幺幺无，follow up是判断环和invalid input
//2. 而舞散，follow up不太记得了，实在不好意思
//3. 给一个matrix，里面是红蓝两种颜色的方块，只flip（颜色红蓝换）最左上角那个方块，规则是，所以与其相邻的相同颜色的方块都翻转，连锁反转，比如
//[0, 0, 1, 0] 翻转一次得到 [1, 1, 1, 0]
//[0, 0, 0, 1]                        [1, 1, 1, 1]
//问最少几次翻转可以让所有颜色统一
//给了暴力dfs的算法，一步一步翻，面试官让写，runtime O(m^2n^2)
//问怎么优化，最后想到了O(mn)的理论解法，没写，后来才想到可以union find做，因为相同颜色的方块实际上已经因为连锁规则变成一个整体了，这是我觉得最难的一轮。
//4. 涂篱笆，给一串intervals，每个interval是涂篱笆的范围，每次返回实际涂色的长度，比如[1, 5], [3, 7], 返回 4, 2，第一次涂色长度4，第二次有重叠‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌，只涂了[5, 7]
//5. 类似于课程表，不过给的是task以及每个task运行的时间和dependencies，tasks可以同时运行，问完成全部task最短多久，
//比如A depends on B和C，B，C分别花费3和5单位时间, 所以A运行前要5单位，因为B,C可以同时运行
//
//// https://www.1point3acres.com/bbs/thread-889468-1-1.html

//22.
//电面: 一个韩国老哥，给你一个语料库，要求你对语料库进行训练，然后根据给定的词语预测出下一个词语。面试官直接说要用bigram和用frequency的标准来做。
//
//比如 ["I like Soccer", "I am Sam", "I like playing piano"], 在这个预料库里，I like 和 I am分别出现了2次和1次，所以"I"出现的时候要预测出like而不是am。题目挺简单的，但是要注意空间优化，我是用dict of dict的解法来优化
//VO1: 印度老哥chrome组的, car race变种
//lc 捌壹捌 但是是让你求到某个点的sequence，不是length
//VO2: 没露面，应该是中东老哥，lc 贰伍叁变种，不过对象不是meeting room而改成了http request
//VO3: BQ 轮，中国老哥，what if your team member takes all your credits, tell me a time when your manager gives you unreasonable tasks, 其他不记得了应该有一个ownership相关的
//VO4: 印度老姐，两道题，一道easy一道medium，第一题 lc 伍零肆变种，不过底为3，直接用二进制的解法秒过了，不过最后要你自己想case，
//要考虑0的case和负数的case。第二题lc 原题 七陆柒，以前做过但是记不清了，最后用了一个greedy和priority queue的解法，最后8分钟没有时间写，想法说清楚了，
//匆匆忙忙写了个代码交上去，代码是错的应该。
//总结：我都是面‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌试官提完问题我先说一下解法，因为现在virtual，我把思路也写在了document上，边说边写思路，保证一直在思考哈哈。说完然后问问面试官这样解可不可行，可行的话就开始写，写完然后跑一下测试，再说一下复杂度。
//
//// https://www.1point3acres.com/bbs/thread-889374-1-1.html


//23.
//Coding 1 - https://leetcode.com/discuss/interview-question/539511/apple-onsite
//Coding 2 - 给一个list of coordinates作为occupied，return每个coordinate最近的unoccupied的点，一样近的任选，order随意
//Coding 3 - 之前地里面说的那个1N2, 2N3, 3S1判断是否valid那个题
//Coding 4 - 给一个list of Folder objects，return每个folder的absolute path。Folder object 有parent和name。然后反过来再搞一遍
//// https://www.1point3acres.com/bbs/thread-889269-1-1.html

//24.
//VO 1bq + 3coding
//// bq：怎么handle new challenge//如何own a project//如果你是mentor，怎么更好地帮助自己的intern//如果在团队里你的选择有小部分人不接受怎么办
//coding 1：第一题是莉蔻伞舞久，第二题是把条件改成如果message在之后的10秒内也出现则第一条消息也是invalid的。
//coding 2：密码锁破解。一共五个数字，每次可以按一个或多个数字，穷举所有的密码输入。
//例如"1-2-3-45"表示第一下按1，第二下按2，第三下按3，第四下45同时按，同时按的几个数字不考虑先后顺序，搜索+二进制状压。
//follow up估计是用二进制优化搜索中的状态。但因为一开始就用了二进制所以这轮20分钟就结束了。
//coding 3：第一题，输入是一个字符串列表，判断能否用全部的字符串组成一个对称的字符串序列，例如["ab", "c", "ab"]可以，但["‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ab", "bc", "cd"]不行。
//第二题是在判断后的基础上输出全部的组合，搜索+回溯。
//
//// https://www.1point3acres.com/bbs/thread-890516-1-1.html

//25.
//提目是猜词给反馈。给一个目标字符串，含无重复乱序英文大写字母，再给一个用户猜测的等长字符串。要求我们写算法来给用户的猜测做反馈。
//规则是如果猜的字在目标里且下标也正确则为对，
//在目标里但下标不正确则为中，
//不在目标里则为错
//输入参数1 哦哈啊嘿哟
//输入参数2 呃哟啊嘻吧
//样例输出  错中对错错
//用好数据结构逐个给出答案即可。
//followup：目标字符串有重复字母，且规则发生变动，
//（猜测的字和目标的字从左往右最近的字匹配），
//每次当 猜测的字和目标的字发生匹配，无论猜没猜中位置，都会把目标字给蒙住，之后不再能使用。例如：
//哦啊啊哦啊哦
//哦哦啊哦啊哦
//对中对中对错
//还是一样，想好数据结构，然后考验的是否能清晰地做好条件判断。感‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌觉面试时还是挺容易糊涂的，因为有好几个条件分支。
//楼主有漏掉一种小情况（还被举例提醒）（其实是误解了题意），有打错语法细节，但是都很快纠正。面试官表示别担心，你已经很好了，算法什么的都很好。
//愉快聊天结束面试。
//有用请加米谢谢还会再回馈地里！
//
//这和贰舅舅很像啊
//
//// https://www.1point3acres.com/bbs/thread-890352-1-1.html

//26.
//第一轮：国人小哥，英语水平和我差不多，不太流畅。计算beauty value，一开始没看懂题目，其实就是给一个list,
//表示每个block的颜色，再给你颜色c，让你计算list里这个颜色的最长连续的长度；follow up1，如果是给一个很长的list of c，怎么做；
//followup 2，如果可以repaint其中m个block，那么最长连续的长度是多少；follow up3, 如果这个block list是一个circle，怎么处理。
//第二轮：不知道哪儿的白人小哥，英语感觉也一般。BQ，most successful project, manager want to deliver unfinished product, move into new team/project, prefer people from same background or different, do something out of duty
//第三轮：国人小哥，人很好，直接就跟我中文面试了，还很积极的给hint，有时候是我反应慢了点，他就提了一嘴。
//Reverse linkedlist odd & even; Maximum path sum from leaf to leaf
//第四轮：黑人小哥，找到几个朋友之间最近的cafeteria，给int[] friendsPos，int[] cafeterias, int[][] edges，用的BFS，
//follow up, 如果cafeterias特别多，怎么办
//第五轮：坐在会议室最远点的小哥，可能是南美的，小哥人很nice，一路引导我实现最优的算法。一种board g‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ame，只有0，
//1, 每次用 boolean next(int val)，就会从board[0][0] 插入val, 并且剩下的数都会z字移动，这时候如果任意row或者col全都是1，就winning, 返回true。
//
//第五题：
//101
//010
//111
//插入1后变成
//110
//101
//011
//插入1后变成
//111
//010
//101
//
//// https://www.1point3acres.com/bbs/thread-890305-1-1.html

//27.
//1. 给几个cd命令，返回正确的directory
//eg: cd a/b/c
//               cd a/e/f
//               cd a/b/h    cd x/y
//return a
//             - b
//                       - c
//                       - h
//                     - e
//                       - f
//                    x
//                    - y
//2. 类似phonetool的一个公司结构，每一个人都有read email time, reporter(下属的员工)，问：当一个leader（root node）发一封邮件出去（只发给自己的reporter，在由reporter给再下一级的人转发，以此类推），需要多少时间全公司的人才能都读到邮件
//3. stream一辆车在每一秒的速度, eg: 第一秒60mile/h 第二秒65mile/h，问： 在每60秒的window里（0-60，1-61，2-62‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌，3-63….）最低的车速是多少
//4. behavior
//5. 流雾尔
//
//// https://www.1point3acres.com/bbs/thread-882180-1-1.html

//28.
//就一道题： 1. one user adding num to a stack in order: 1, 2, 3, ...n
//Another person pops num from the stack at any time and prints the number out.
//Given a list, determine if the list is valid.
//Example: input[1‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌,2,3,4,5] true
//input [2,5,1,4,3] false
//
//// https://www.1point3acres.com/bbs/thread-887030-1-1.html
//
//29.
//第一轮BQ: 中国大哥
//1.你是怎么做的能保证与科技与时俱进
//2.如果你被安排了你不会做的domain,你要怎么做?
//3.在与其他的组交流的过程中 有什么超越自己责任的故事吗
//4.不同的意见
//5.如果你和你的同事有不同的工作方式, 你怎么适应他的?
//第二轮: 中国小哥
//有一个grid里面有blocker, 有一个机器人,给定一个位置, 这个机器人只能往右走,每走一步effort=1,这个机器人也可以上下走(可以跳跃,例如越过blocker,可以去同一列的任何位置) 不管跳多远effort都是1. 求抵达最后一列任意位置的最小effort
//0   x   0
//R.  x   0
//0.   0  x
//第三轮: 印度小哥
//Google有很多campus 然后你的小伙伴在不同的campus, 中午约饭, 约到cafe, 找到最早能集合的时间的时间(所有的小伙伴都到了那个cafe). input: List<campus>, List<cafe>, List<int[]>edges(例如 postionA-PostionC)代表A到C之间有路, 假设每一次移动的时间都是=1min
//第四轮: 中国小哥
//原题:两个时间点之间最短的时间差wu san jiu
//第五轮: 美国小哥
//有一堆product,每个product有个ID, 每个product他会有一堆offerID, 每个offerID对应的一个price
//1. add Of‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌fer(pID, offerID, price)
//2. remove Offer(offerID)
//3. queryClosetPrice(productId, price) return offerID
//
//// https://www.1point3acres.com/bbs/thread-883699-1-1.html
//
//
//30.
//第一轮 coding
//给一个有向连接图表示不同service state 之间的转变方向，只有其中一个是healthy state。问给定途中的其中一个state，
//如何知道是否一定能self recover道healthy state。
//第二轮BQ
//项目经验 技术经验 聊天
//第三轮 coding
//实现一个data structure，来满足每次enqueue entry 都只process10秒内没有出现过的entry
//第四轮coding
//给一个矩阵表示灯泡开关，每次操作都会flip一整排或者行，判断能否关掉所有灯
//第五轮sys‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ design
//设计一个可以fetch top k frequent log的系统，就给这一句话，细节都得自己问
//
//// https://www.1point3acres.com/bbs/thread-886633-1-1.html
//
//31.
//第一轮的背景是在一个grid里面有好多路由器，（每个路由器在一个坐标的位置）， 然后两个路由器相距10以内被视为相连，
//然后路由器可以通过相连的路由器到达新的路由器，给两个路由器看是否能够相连， 蛮简单的graph，
//follow up 问如果只有最近的路由器被视为相连要怎么改，还有如果grid太大怎么改，
//还问我知不知道KD Tree（好像是这个， 我真不知道，我就说不知道了）， 面试官人很好，说没关系，他也不咋懂
//第二轮是给两个数组，比如说[B, C, A, D]和[A, D, C, B]，后面的数组是这些字母的新order， 问使用bubble sort的话需要最少多少步能够从第一个array变成第二个，同样建了给图。。。然后之前忘了bubble sort是啥。。。（本科基础没学好）， 小姐姐给讲了一下，最后写完了问了一下时间复杂度
//第三轮还是图。。。好像是地里出现过的friend 去 食堂的问题，给一个friend的数组，一个食堂的数组，一些edge，最后问所有friend都能去到的最近的食堂，这一轮说了好多话写了好多，面试官中间纠正了一个想法，然后我追加用了两个map来存这个食堂被几个friend去过，还有到这个食堂的距离，最后写完了，面试官说有一些typo，但是应该是work的，不太清楚具体反馈咋样。。。
//第四轮就是纯BQ，问了好多问题，成功的项目，有没有习惯不同的组员，和其他组一起做事，在一个组里工作最主要的能力之类的，面试官蛮面善的
//第五轮事地里出现过的面经题cord Tree。 可以看这个link： https:‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌//leetcode.com/discuss/interview-question/1593355/Google-or-Phone-Interview-or-Rejected/1162223
//问了构建cordTree，根据index找字母，还问了substring，最后substring时间不够了，说了下思路没写完
//
//// https://www.1point3acres.com/bbs/thread-886643-1-1.html
//
//32.
//一小時之前才剛結束的的店面
//題目類似 離扣 傘肆漆
//題目
//你有一組聊天對話log，找出 username of top K word count
//log 格式為 <username> saying
//word 為連續的 a-z A-Z 0-9 字符
//Ex.
//<ABC> Hello~ my friend!
//<XYZ> Hi ~~yo!!!!
//<ABC> Are..you.. there?
//<XYZ> I'm here~~~~
//ABC 之 word count 為6 (Hello my friend, Are, you, there)
//XYZ 之 word count 為 5 (Hi, yo, I, m, here)
//假若 K 為1, 則 output為 ["ABC"]
//Follow up:
//如果今天有非常非常多不同的userna‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌me
//但我們需要找的K很小的時候
//你會如何處理
//'
//// https://www.1point3acres.com/bbs/thread-886606-1-1.html
//
//<HERE>
//
//33.
//小数点相乘运算
//input: an array of float, output: float number
//第二关是
//input: interge of 2-d matrix, output: a maximun number of a path
//问题是从0,0走到n-1,n-1,路径找小的那个数字，然后回传最大的数字，有点近似leetcode的path-with-maximum-minimum-value
//这当中我选择使用deep first search,
//但面试官认为可以n*m用‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌DP解，有写过这道题的你怎么看？
//
//// https://www.1point3acres.com/bbs/thread-886435-1-1.html
//
//34.
//天面了两轮Google onsite，有一轮coding的题感觉很有意思，现在想发出来大家一起探讨一下，题目大意如下：
//有12个size为1的tile stack，共有4种颜色，每种颜色的tile为3个
//游戏规则是A、B两个player轮流merge stack
//一次只能merge两个stack，merge的条件是（两个stack的顶部tile颜色相同）或（两个stack的高度相同）；一次merge只能把一个stack整体堆到另一个stack上（不能中间插入）；可以merge任意两个stack，不需要它们相邻
//如果轮到A或B其中一方时无法再merge stack，那另一方就获胜
//假设A和B都play optimally，要求输出A和B最终谁会赢
//举个比题目简单的例子，假设现在有4个stack，2种颜色 红色（R）蓝色（Y）
//那初始状态就是 R | R | Y | Y （四个stack，每个stack有一个tile），现在轮到A先开始
//那么这种情况下，假设A采用最佳策略，那A必定会选择merge成 RR | Y | Y 或 R | R | YY，这样后面B只能选择merge成RR | YY，然后A merge成RRYY，B无法再merge，A就赢了
//A当然一开始还可以merge成 R | RY | Y，但这样B可以merge成 RRY | Y，那A就无法再merge，B就赢了。所以如果A先手并且采用最佳策略的话A显然不会这么做。所以这个例子的最后结果就应该输出A赢。
//同样的问题，如果扩展到12个stack，4种颜色，显然比上述例子要复杂很多，每一步都有很多种可能性，且关键点是我们如何确定什么是最优策略？显然每一步的走法都‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌需要考虑这么走的情况下，另一个player的future moves，不知道大家有什么想法没有？
//
//// https://www.1point3acres.com/bbs/thread-886419-1-1.html
//
//
//35.
//一道题
//就是给几个x,y坐标。x,y坐标被占领了。
//求最近的没被占领的坐标。 我原本想说用bfs。其实可以了，只是我coding那个时候大脑没反应过来。
//先看看四个面有没有被占， 如果全被占，再吧四个面放进queue里面，如果只要有一个没被占这就是最近的坐标。要不继续bfs找下去。
//这是brute force的办法。这个办法紧张到都没写出来。
//所以连优化的方式都没法讲了。所以跪了。太惨了。估计还是准备不充分导致。我太弱了
//
//原题是这样
//给一组  坐标 Points{ int x, int y}
//List<Points> Input;  
//input.add(new Points(0,1));
//input.add(new Points(1,0));
//input.add(new Points(-1,0));
//input.add(new Points(0‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌,0));
//input.add(new Points(0,-1));
//这些点就是被在 xy图上被占的点。求 离他们最近没被占的点。
//所以答案也是List<Points> res  应该也有5个
//比如0,0 最近没被占的点，可以是1，1， 2，0， -1,1 ，取其中一个就行。简单用bfs就能求。问题是求所有。然后我就卡住了。
//
//// https://www.1point3acres.com/bbs/thread-886192-1-1.html
//
//36.
//题是给用户和网址和时间的边，第一问是建图，第二问是有多少u尼克路径。
//上来没沟通好怎么才算u尼克路径就经典bfs了，写完小哥说你要要你的code吗，有些case不对，然后直接给我画了个图说这情况不行。当时有点懵，
//就把set改了一下能记录路径的信息，最后也没有写完。。。后来也通知过了。
//
//第二题是求用户a和用户b间的u尼克路径。
//// https://www.1point3acres.com/bbs/thread-885499-1-1.html
//
//
//37.
//第一题
//有n个m位的id，请问最少需要遮住其中多少位，才能让他们看上去相同
//我解法是hash统计每一位上的数字个数，等于n的即可不用遮
//然后写的时候太急了，循环内的变量和nm写混了，被面试官指出来了，然后之后问了复杂度和测试样例设计
//第二题
//在第一题的基础上，最少需要遮住其中多少位，才能只剩下2种
//一开始太紧张了，直接说爆搜，然后对方让我算复杂度，一算上天，遂重新想
//之后中间卡壳了，求提示，当时并没有理解。
//最后我回答的解法是，先处理每一位上只出现1种和3种的数字，前者不需要mask，后者必须mask
//然后，剩余位数上，如果有一列指定不mask，那么其他的只有唯一解，到这里时间没了
//事后想了一下，后续还‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌可以简化，可以用标记，然后各个位上只搜索一遍（因为搜过的没必要再搜一遍了，估计他给的提示是这个）
//
//// https://www.1point3acres.com/bbs/thread-884854-1-1.html
//
//
//38.
//其中有一轮coding是中国人面的，感受特别差。 题目是：
//有一个datastream, 不停收到到transaction price,   23.21， 44.67 这种数据。
//建一个object， 两个function: insert(), getPercentile(percentile: float).
//insert拿来 insert price to data stream,
//getPercentile用来算percentile, 比如 getPercentile(0.75). return P75 price
//拿到题目想到了利口 295。 然后提出了用priority queue, linkedlist 各种办法来解决这个问题，但是依然 insert 怎么做都是log(N)复杂度..
//最后是在是找不到insert是O(1)的办法，就问面试官， 他说把 可能的 price * 100,  然后建一个 最大值price 的 array<int>，
//比如收到 $100.25 这个价格，就等于 array[idx = 10025] +=1
//题目上没有给价格的range, 所以我也没有往这个方向想，事后估算了一下，如果要建一个 2**31 * 100  的 array<int>，大概需要800个G的存储空间
//所以发出来问问大家讨论一下...
//其他题目都很标准，coding 算法本身不难，但是狗家很喜欢在上面加OOD,所以有些地方还是有很多坑
//coding1: merge interval ood 版本，2个function, insert interval, get(int) -> return if int is in the interval list
//coding2: 设计一个 暴力解锁器， 假设有一个 锁，你可以通过 turn(idx)来旋转它的值，比如 turn(1)， 会把 0010 变成0020. 然后写个程序走完所有的 可能性。
//coding3: 上面那个奇葩题
//design: 设计一个logging service，题目很直球，但是考了很多 system design 的东西， 比‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌如 proxy, api gateway, queue, 什么的
//behavoir: 聊一下工作经历
//// https://www.1point3acres.com/bbs/thread-884120-1-1.html
//
//39.
//1. 刷体网 散四遛 暖身，我用deque做，有很多东西都要自己clarify或是自己定义，需要好好沟通
//2. 延续上题，问说能不能找最大数，概念上就像 耳伞久 ，但是是要求像上题一样以“数具流”的形式
//// https://www.1point3acres.com/bbs/thread-884105-1-1.html
//
//40.
//和魃毵糁很像但是结构变了。求米！！
//我的解法是哈希表
//
//// https://www.1point3acres.com/bbs/thread-883776-1-1.html
//
//41.
//第一轮：是一个欧洲口音的人，迟到15分钟，全程在玩手机，我都无语了，每次问问题，问完，我都得问一句Hello？才有反应。
//One of the TTY command tools got updated, its input/output has changed
//A: tool在一个chain里, how to make sure the TTY command is backward-compatible (A: use wrapper)
//B: you got several call chains, a->b->c->f, d->c, e->b->c->g, create a structure to store these call chain, and if updated c,
//find which tool need to be noticed to update
//我具体忘了这个题他说的啥了，最后实现了一个trie search
//第二轮：大概是个美国人，
//8 directions: N\W\E\S\NW\NE\SW\SE
//P1 is North of P2
//P2 is North of P3
//P3 is North of P1
//Output: False (since P3 will be in the South of P1)
//使用Topological sort来解决
//# P1在P2的北边，P1在P2的东边 = P1在P2的东北边
//# topo sort， 我用的indegree做的，他没搞懂为啥这个能找到cycle，让我用dfs写。。我xxx
//第三轮：System Design，听口音是东欧人，迟到10分钟，我xxx
//You own a network and design a system to manage resources distributed to users. You need to control the traffic and limit the bandwidth of upload/download according to the users'' plan (like the premium plan will have 10GB/s) 类似于，运营商提供不同的plan，用户购买了每个plan，有两个限制（1. 速度，2. 总量）
//1. handle how to control in one location and multiple locations
//2.‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ multiple users in one location
//3. consider multiple devices for one user
//4. how to broadcast among different locations (User A has two devices using diff gateway)
//5. what data to record in the gateway
//6. how to manage the traffic in one gateway, how its bandwidth be distributed
//最后还写了一段代码，我用的是interval overlap里面的hashtable来count
//第四轮：亿污凌
//第五轮： BQ，直接no show...
//问的问题，就是challenge project，项目怎么own
//
//// https://www.1point3acres.com/bbs/thread-883638-1-1.html
//
//
//42.
//BQ，感觉没答到点上的话面试官会再重复一次问题
//给了个奇怪的背景说人行道下雨，其实就是streaming的刷题网勿留
//幺而勿斯变种，找被土地四面包围着的水
//很简单的给一大堆unsorted时间点，每个时间都有人进进出出求最多人数，follow up就是某个时间点大量发生
//而衣儿巴，但是我傻了直接做了那个trick。
//
//// https://www.1point3acres.com/bbs/thread-883495-1-1.html
//
//43.
//1. 东南西北以及四个斜的 八个方向 比如1N2,2N3, 3S1就是说1在2的北边，2在3的北边。3在1的南边（也就是1在3的北边），代表着1->2, 2->3, 1->3，此时返回true，
//Answer: 找circle。如果有circlereturn false。没circle return true
//2. 建一个MyIter class。假设已经建好了trie(ABC → “he”, DE → “llo”), 。trie的leaf是一个string。
//MyIter class的constructor input是一个javaioreader和一个已经建好的Node root
//Answer:
//弄一个buildStr method，给你一个ABCDE这种String，返回在tire里面的leaf的string的concandinate → “Hello”
//Iter class还要写出hasNext和next method。这里可以把之前建好的string在class里面存下来，然后用一个index去做。
//follow up：因为给的Node的children 是个node array，如果换成hashmap会怎么样
//follow up：如果换成换hashMap的down side是什么
//follow up：在memory层面还能如何优化？
//3. 有个startRouter, 有个endRouter，有个RouterLocation Array，有每个Router可以connect的range。
//看是否可以从start connect to end
//union find。遍历每个pair，然后union在一起，然后find startRouter, find endRouter. 看他们的parent是否一样。一样就说明可以从start到end
//4. 第四题 Wrap text 有点像利口留疤但其实不是。
//Wrap text.
//A function which takes text and a width (int) and returns the number of lines that the text would take on the page.
//Example:
//text = "asdf asdf asdf asdf asdf asdf"
//width = 7
//answer: 6
//Example:
//text = "asdf asdf asdf asdf asdf asdf"
//width = 8
//answer: 6
//Example:
//text = "abcdabcdaabcdabcda \n\tasdf asdf(1) asdf asdf(1) asdf(1)"
//width = 9
//answer: 5
//text = "asdf asdf asdff   asdf asdf asdf"
//width = 14
//asdf asdf
//asd       asdsadsd
//asdf asdf
//text = "a a a a    a a a a a a a a"
//width = 16
//answer = 2
//a a a a    a a
//a a a a a a
//text = "a          a        a a"
//width = 12
//s = s.trim();
//split("\\s+")
//After split
//for each single string, we need to use it and width to get how many lines it take
//*/
//我开始以为是利口留疤就按照那个思路去做了。这题corner case巨多，我现在也有点忘了具体要求，
//只记得一个特别长的String是可以跨越多行的，然后两个String中间如果有多个空格也不像留疤那样要缩减成1一个。当时很勉强做出来了。
//Follow up: Assume Part 1 getLines works perfectly.
//Write a new function:
//int getColumnTextSplit(String s, int width, int col);
//if width = 10, col = 5
//then we have two columns of width 5
//width = 10, col = 2
//two columns, width 2 and 8
//returns the split point in s
//text = "asdf1 asdf2 asdf‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌3 asdf4 asdf5 asdf6"
//width = 12
//col = 6
//asdf1 | asdf4
//asdf2 | asdf5
//asdf3 | asdf6
//asdf1 | asdf2
//| asdf3
//| asdf4
//| asdf5
//| asdf6
//这里的col是指在哪一个col开始分。通过这个col可以得到两个width。然后根据这两个width，再从原String里面切一刀，
//然后左面String根据width1，右面String根据width2，得到两个lines number。目标是这两个lines number尽可能的靠近。
//没做出来，面试官最后说了可以用Binary Search。估计就给我down level在这一轮了。
//Timeline:
//HR phonecall 2.16.2022
//Pho‍‍‍‍‍‌‍‌‌‌‍‍‌‌‌‌‍‍neScreen Skip
//Onsite 3.17.2022
//HR电话说positive 送HC 3.24
//通知downlevel. 3.30 只能说自己面的不行，HR人挺不错的，当时没有close因为我还在等其他offer。
//Case close 4.5 拿到了其他offer都是L4，这个就close了。下次再战
//
//// https://www.1point3acres.com/bbs/thread-883437-1-1.html
//
//
//44.
//1st round. 技术面
//cord tree, 可以看蠡口的讨论区cord
//（a）对leaf, internal 分别定义一个类，后续实现用isInstance() 区分
//（b）对给定的index，返回对应的字母  树的遍历即可，注意corner case，这里有被面试官提醒，不知道会不会扣分
//（c）在（b）的基础上，对给定的index，输出index~index+L的字符串，还是树的遍历+path 记录
//（d）time/space complexity
//2nd round. 技术面
//secret & guess 有点像 蠡口的二舅舅
//secret：CHALK
//guess:   AGBLC
//output:  YRRGY
//就是 如果字符存在且位置正确，G；字符存在但是位置不对，Y；否则 R
//（a）secret 每个字符是unique，guess 也没有重复字符
//（b）secret 每个字符是unique，guess 有重复字符 ， 比如说 guess=“AGAIN”, output="RRGRR"
//（c）secret 有重复，guess 有重复字符，我用了dictionary 记录count
//（d）有一系列 guess 输入，让你比较每次的guess 是否有比上一次的guess 更好，没有implementation, 主要就在定义 何为“更好”。比如说 R->G 是一种improvement, 到这里我就有点云里雾里了 不知道在考察什么，然后就差不多到时间
//3rd round. 技术面
//snapshotarray....hhhhhhhh 正好周末刷到 蠡口瑶瑶撕溜
//讨论区大佬用了binary search 和记录每个index 的 history record.
//我自己用了个dictionary 记录每次的变化，但是空间复杂度有点高，最后follow up 我说了一下大佬的思路
//4th round. BQ
//一个笑眯眯的印度小姐姐。
//就是一些 disagreement, conflicts, a happy experience, high goal
//5th round. 技术面
//filesystem, 也是个树结构，多叉‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌树, 有dir, 有file, 然后file 有对应的filesize, 让你计算这个目录下的filesize 总和
//（a）DFS 写了一个
//（b）某些目录被多次访问，如何提高计算效率。用一个dict 记录
//（c）BFS 又写了一个
//（d）如果有cycle 怎么办，变成一个图的问题，用一个visited set 记录已经访问的节点
//
//// https://www.1point3acres.com/bbs/thread-883204-1-1.html
//
//45.
//1. BQ。抢功劳的同事；紧急的deadline；unexpected problem。根据面试官的反馈，感觉还行。
//2. 路由器题。输入是一堆路由器的(x, y, id)，每个路由器可以给radius范围内的其他路由器发消息，给一个初始id，和一个结束id，
//问消息能否从初始路由器发送到结束路由器。
//follow-up是，路由器只能给radius范围内最近的路由器发信息，选最近的路由器发消息；
//如果一个路由器有好几个最近的路由器，给所有的最近路由器发消息。
//我用O(n^2)建图+BFS解决所有，一直写写写，没怎么优化。这一题不知道怎样。
//3. 实现一个class，在xy平面上，一开始没有点，不断add和remove点，点用坐标(x,y)表示，每一步return一个最小box把所有的点框住，box平行于x/y轴，
//要求每一步优于O(n)解法。
//我propose一个x2ydictionary，对每个x对应的y，double linked list O(1) delete, O(k) insertion，接着在面试官的引导下，
//用BST存x，但是python没现成的BST。凉凉。面完觉得面试官想要BST of BST？我觉得是个Hard。
//4. 面试官一开始想考上一题，不想重演上一题的灾难，后来改成manager/employee题：一个class，
//一开始为空，有三个method，setManager(M, E), M是E的direct manager，setPeers(E1, E2),
//E1和E2是peers，E1的direct manager不是E2的，queryManager(MM, E)，
//问MM是否是E的manager，可以是很多层的manager。这三个method被call的顺序是随机的。
//我的解法：dag表示manager-employee关系‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌，udg表示peers关系，另外一个e2m字典，记录直接mgr，
//时间刚刚够写完，答了下时间复杂度。不知道怎样。
//5. 霸一罢。一个车，初始速度为1，初始位置是0，两个指令，A速度加倍，R速度降为1位置不动。
//第一问，给一个instruction string，返回离初始位置的distance。第二问赛车本车，给最终距离，返回最短指令序列。
//抽到这题心里就拔凉，知道第二问要dp，解法没太看明白。第一问写完问了时间复杂度，dry run with AARA。第二问尝试用那个dp解，没写完，
//事后后悔没用BFS写个brute-force。
//四个题两个题考到盲点，上周四微软碰到两个hard一个黑人面试官，面完就有点自闭。今天面完更自闭了。亚麻和🐶家放大水为啥放不到我身上？
//
//更正：manager/employee那题，setPeers（E1，E2），E1的direct manager是E2的，如果E1是C的manager，E2不是C的manager
//
//// https://www.1point3acres.com/bbs/thread-883080-1-1.html
//
//46.
//第一轮：面筋原题bad comitt详见https://www.1point3acres.com/bbs/thread-816299-1-1.html， 这个帖子里面代码都有网友贴了
//第二轮：BQ轮，有人抢credit怎么办，diffrent culture/background，队友不出力怎么办，还有其他的
//第三轮：面筋题，manager - employee关系，详见https://www.1point3acres.com/bbs/thread-850871-1-1.html  和 https://www.1point3acres.com/bbs/thread-841619-1-1.html ， 这题我看了但是没有自己面试前implement，面试的时候写的磕磕绊绊
//第四轮：题目细节有点忘了考的binary search 类似刷题网1011、410，思路基本一模一样
//第五轮：感觉这题是小哥现想的，也不知道想考啥。input是好多log string ex："1+3+5=9",其中1，3，5是三台机‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌器的运行结果，
//9是expected结果总和，如果出现"1+3+4=9"那就是有一台了（这里假设只有一台机器出错），问怎么查有没有机器出错，如何查是哪台机器出错，
//然后思路就是把这个string两部分parse一下看前面的和是不是等于后面的数，还有一些follow up有点忘了总体感觉就是这题有点无厘头，没啥参考意义
//
//// https://www.1point3acres.com/bbs/thread-882904-1-1.html
//
//
//47.
//一个2D array，里面0是空地，1是墙，问从左上到右下，最多拆一堵墙，能否到达，用了DFS和BFS两种解法。
//Follow up是不问你能不能到了，问你最少拆几堵墙能到，套dp没解出来，就说用搜索暴力搜也能搜出来，就是time complexity是exponential的，
//最后提示（说是提示基本就是告诉我答案了）用weighted graph做，可以用Dijkstr‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌a's Algorithm 解'
//
//// https://www.1point3acres.com/bbs/thread-882732-1-1.html
//
//48.
//1. Job Scheduler, 拓扑排序解决， follow up： 如果跑在实际场景中会遇到什么corner case
//2. Friend Host,  Union Find 解决， follow up: 怎么优化 time complexity
//3. 耳零散肆
//4. Meeting room, 这个题目最近很高频，有个朋友比我先一天面试也遇到了这个题目， 也是地理出现的那个面经题， 先写了sweep line， 后用priority queue优化了
//5. BQ, 跟地理看到的差不多，leadship, conflict, how to help you teammate
//// https://www.1point3acres.com/bbs/thread-882447-1-1.html
//
//
//49.
//Round 1: BQ，面试官准时上线，不是来自GCP。问了大概conflict 和 success，failure，challenge这类的。 没想到第一轮是个BQ。
//面试官人很chill， 也很nice，问答的时候都有跟我互动，讨论我说的答案。体验很好。
//Round 2 Coding, interviewer no show...... 我按confirmation 邮件指示等了10 分钟后联系recruiter, recruiter 及时回复并且跟我讨论方案
//最后决定今天所有面试结束后，加一轮。recruiter很给力，但是，早上喝完一大杯咖啡状态最好的时间过去了。
//Round 3 Coding, 俩面试官又一次迟到，不过大概10分钟内上了，一顿道歉。俩面试官一个drive，一个shadow，都不是来自GCP。
// 问的题涉及矩阵，难度适中。这里自己菜了，没想到能简化问题的trick，探索了几个可能得optimal solution后发现无法解决问题，
// 最后只能硬着头皮写最笨的办法，最后没写完。其实我觉得如果这种trick类问题，面试官给点提示无妨，可惜我的面试官全程看着我各种试，也
// 没给啥能确定方向的hint，也没怎么参与讨论。面试完后上厕所的时候瞬间想到了如何简化问题，可惜已经晚了。
// 不过还是又一次确认了厕所是solution 的发源地。体验一般般吧。
//Round 4 Cooding。俩面试官又又又又迟到了。都不是来自GCP，而且进来打完招呼后，其中主面试官掉线了，我跟shadow 尬聊了下他们哪个组啊，
//都干些啥projects啊。最后主面试官上来了也没时间寒暄了，直接进去coding 正题。题很简单，字符串处理。写
//完solution，follow up 有点像OOD，让我如何扩展。我现场写了几个design patterns，delegation pattern, strategy pattern, chain of responsibility 这类。然后follow up了些问题后时间到了结束。
//Round 5 System Design, 面试官又没准时，不过5分钟以内上了，好像是因为办公室设备处问题了，就算他准时吧。面试官来自GCP。问的比较底层的系统设计问题。人很nice很有耐心，问的问题都很constructive， 都是为了最终优化解而问。问题没有提前准备过，所以都是现场思考，优化。最后给出的结果面试官很满意，并且肯定性的说了一句google就是用的这种架构。当时还是蛮欣慰的，几年的工作经验和各种conferences还是没白去。体验很好。
//Round 6 是为早上no show 安排的临时加试，面试到这里，已经从早上10点高度集中到下午4点了。面试前已经饿的头昏，有点累了。吃了一包曲奇，喝了一杯牛奶，硬上。结果，面试官又又又又迟到了。。。10分钟以内，算了。面试官不是来自GCP。上来寒暄几句，给了一道binary tree 的题。难度中等偏hard，对性能要求很高。因为已经很疲劳了，尤其上一轮SD 消耗很大。脑袋空白了两分钟之后想到了半优化的办法。然而，在我写代码的过程中，这个面试官mute了自己，然后‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌开始在那各种伸懒腰，伸胳膊扭头，打哈欠，我余光里一直有他在那晃来晃去。我心里已经十分不爽快了，大哥，你累了，我也累了啊。。。最后我写完了，跟他解释我的solution的时候，他还当着我的面冲我打了个打哈欠。。我也只能硬着头皮当啥也没发生继续解释我的solution。最后提到了如果提前处理数据，牺牲空间，优化时间，面试官也算是满意了。
//
//// https://www.1point3acres.com/bbs/thread-882397-1-1.html
//
//
//50.
//第一轮：散溜溜变种 要求从最底层子节点开始remove整个tree。
// follow up： 1.移除顺序变为移除了某个叶子的子节点后立即移除该节点  
// 2.如果给定了移除顺序 判断是否valid （valid定义是必须先移除该所有子节点再移除母节点）
//第二轮：尔流酒变种 比原题简单 给定字母顺序判断string valid与否。
//follow up：分布式，多个机器怎么做这道题，机器之间transfer什么变量，
//如果字符串长度少于机器数量怎么办，runtime
//第三轮：Manager chain，给一些 {index， 员工邮箱，经理邮箱} 然后输入一个员工输出manage chain，follow up就是判断环
//3/29 一轮coding+ bq被鸽
//第四轮：string substitution。 输入 {last： li}，{first： zi}， {name：{last}{first} } 
// 然后给定一个 “hi，{name}”就变成“hi， lizi”。follow up是想edge case 和写throw exception
//第五轮： 被取消了
//这里不得不提一下这里极差的体验，面试前一天半夜告诉我取消了还是另一个recruiter给我发的消息，
//然后重新安排面试的recruiter从来不回消息，一周以后突然来了一封安排在了4/6（也没问我的时间安排，直接就schedule了）
//4/6 一轮bq
//面试官上来就要给我面coding，我说我已经做了四轮了。然后就又和recruiter联系，
//十五分钟以后确定了面bq。印象深的是如果公司环境不好，同事相处不佳，你作为员工怎么办+你作为director怎么‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌办
//
//// https://www.1point3acres.com/bbs/thread-881535-1-1.html
//
//51.
//1. youtube组的黑人小哥
//开放式的OOD，设计一个播放长视频的播放器。长视频被分成小段视频， 存在blob storage
//实现几个功能：play，pause，stop，seek
//这轮有点没想到没准备 面得一般
//2. 一个安卓staff级别大佬
//从巨简单的一维presum [1,2,3,4,5] => [1,3,6,10,15]
//到二维presum [1,1,1] =>  [1,2,3]
//                        [1,1,1]       [2,4,6]
//                        [1,1,1]       [3,6,9]
//到实现1.update其中某坐标的值 2.get2dSum返回加和后的某坐标的值
//讨论怎么节省时间复杂度，怎么保证concurrency一致性
//最后听大佬吹了20多分钟nb 去过世界哪个google office 跟哪个大佬谈笑风生 刚开始安卓团队就几百人 办过多少讲座
//超时很久但是幸亏和下轮之间一小时break
//3. 一个烙印哥
//给一个n叉树，返回每条从根到叶的最大值
//输入是root
//比较简单 dfs 到叶 pass down 最大值
//follow up：如果输入是所有的edge，怎么改
//可以改成tree的结构，也可以用adj list
//这轮感觉还行
//4. 烙印哥 系统设计
//设计一个公司内部API quota enforceme‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌nt系统 限制各个API的QPS，resource的QPS，user的QPS
//
//// https://www.1point3acres.com/bbs/thread-881050-1-1.html
//
//
//52.
//VO1 白人小哥 问题是给一很多组tuple {A, B, TEAMMATE} （A和B是队友）, {B, C, MANAGER} （B是C的老板）
//然后给{A, B, Query} 问A是不是B的manager。用了Union Find变体，小哥说以前听人说过用Union Find但是没人用过很吃惊。https://massivealgorithms.blogspot.com/2016/08/google-manager-peer-problem.html
//Follow Up是 给一堆 Query 和 中途老板switch换来换去怎么搞。
//VO2 国人小哥 人很好 问题是给一堆DISK，有些DISK有child DISK。要删除DISK必须得先删CHILD DISK，给一个删除的顺序。用了BFS(degree + graph)来解决。
//小哥刚开始皱眉头，好像他的解决方法不是这样，但是最后跑了一个sample他表示理解了。
//Follow是分批怎么解决，在BFS里面用一个for loop把当前的disk一次性都跑完再跑下一个level的就行。
//VO3 国人小哥 人不错 问题是给一个list，求问一个范围内最大的数字。刚开始惊讶住了，for loop做了一下这么简单的吗，不敢相信，把corner case写出来后终于知道这是个开胃题。follow up是数据很长很长，query范围的次数也很多次。问怎么办，我就用了segment tree。写完了表示可以。
//VO4 国人小哥 人不错 问题是 给一个string 比如 a-(b+c+b) 输出 a-2b-c。聊了半天给了口述了两个解法。用for loop走recursion和用stack。小哥内心的标准答案是stack。但是我用了for loop。sam‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ple run后表示可以，但是第四轮真的有点头昏脑胀了，表现不太好。corner case也没有确认，代码错误出了很多比如 已经有一个global int i了我还define了一个local int i。。。。希望小哥不要给一个差评
//VO5 华裔小哥，BQ，问了一些常见的题目-啥motivate你好好工作 和 不常见的题目怎么激励你队友帮你（答share credits和objective to make our product better）。努力踩点上去，但是感觉表现不太好。
//
//// https://www.1point3acres.com/bbs/thread-881048-1-1.html
//
//53.
//遇到这么个题目，有四个工作，分配个两个worker，每个workēr对应每个task的cost记录在数组中.
//求将工作平均分配，最小的cost是多少。知道要用dp求解，但是没有头绪
//
//// https://www.1point3acres.com/bbs/thread-880998-1-1.html
//
//
//54.
//两年后再面狗，第一轮店面：给一个2d矩阵，0和1分别代表水和地，求最大地的面积。
//第二问：如果一个水被周围8个地环着，那么这个水就变成湖了，湖也算做一块地的面积，求最大地的面积‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌。dfs秒了。感觉是lc上的题，忘记题号了
//
//似乎是distinct islands变形
//follow up 是巴尔奇吗
//
//1 . 695
//2. 第二个是这种类型的包裹吗？https://www.techiedelight.com/replace-occurrences-of-0-surrounded-by-1-matrix/，还是说只有8包裹1那种
//
//// https://www.1point3acres.com/bbs/thread-880944-1-1.html
//
//
//55.
//按照狗家的面筋准备了挺久的，一题都没考到，就很神奇，而且题都不难。
//第一轮，上来就说是类似OOD,design一个类似excel sheet可以compute function的功能。
//比如说A1可以输入MAX(B1, B2, B3),然后B1可能是另一个func，比如 AVG(C1, C2) etc。设计这个，写model和这个function的impl。
//第二轮，比较简单，给一付牌，不是传统的牌，但是类似的是有suit和rank，输入是一个list的string，
//类似S1R3，意思是suit 1， rank 3，实现一个‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌输出最大flush的func。
//第三轮，也比较简单，吴柳 和 酒儿药
//第四轮，design，设计了一个油管选500个中奖观众的系统。
//第五轮，BQ
//// https://www.1point3acres.com/bbs/thread-880599-1-1.html
//
//56.
//Phone Screen:
//# given a sorted request list, (start, end_time). At each starting time, print out how many requests are still running.
//        # sorted request [1,100], [2,3], [5,7], [100, 102]
//VO:
//1 有一个mapping F, 对于非负整数 x1 > x2, 有F(x1)>F(x2). 给定F 和 y，找到整数x使得|F(x) - y| 最小。
//follow up是 非负整数扩展到所有整数。F 没有具体形式，直接F表示。clarify好久。
//2 一个微波炉可以输入时间，形式可以是多少秒可以是MM:SS。 这个微波炉可以输入如1:90这样的形式。一个个数字按。现在定义points,
//每按一个数字就是+1 point，切换到下一个数字就是+2 point。给定一个target时间，你可以替换为+- 10%内范围任何一个数，
//使得point最小。clarify了好久。
//3 给定两个list [‘ac’, ‘ed’, ‘cc’], [‘cba’, ‘def’, ‘ccc’] 求第二个list中满足以下条件的数量：
//能通过第一个list的string加上任意字符后shuffle得到。
//‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌4 cord tree， leaf节点存放长度和string。internalNode节点存放左右指针和总长度。给定根节点，实现查找任意index的字符，以及找[start, end]的子字符串。
//
//// https://www.1point3acres.com/bbs/thread-880554-1-1.html
//
//
//57. OA
//第一题 Find the length of the longest substring that every character has the same occurrences
//第二题 利‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌口 耳腰齐拔
//
//// https://www.1point3acres.com/bbs/thread-880360-1-1.html
//
//
//58.
//第一輪為BQ 主要靠經驗回答，水是一定得灌的；問的問題包括
//remote work 要怎麼跟同事保持良好關係
//組裡有新人怎麼幫助 Ramp up
//假設組裡有個work relationship不太好的組員如何搞好
//其他 暫時想不起來...
//第二輪Coding斯拉夫大哥
//Given a binary tree,
//return the node to root map in any orderGaps to clarify:
//the data structure of tree node, whether the value of each node is unique.
//Ex:
//                 5
//                /  \
//              3    2
//             /  \   /  \
//           1   4   6   8
//Return {5 : [5], 3 : [3, 5], 2 : [2, 5], 1 : [1, 3, 5], 4 : [4, 3, 5], 6 : [6, 2, 5], 8: [8, 2, 5]}
//Follow up: what if it’s an n-ary tree?
//Note: 當時答完 dry-run 的時候卡了一下 follow up 沒時間寫，只有單純講思路
//第三輪coding
//國人小姊姊
//Given an array of integers representing the error rate (0 - 100) during that hour,
//a certain time and a threshold, determine whether or not the weighted error rate that
//includes the given time exceeds the threshold.
//The weighted error rate is calculated by multiplying
//the minimum error rate within the hour duration by the duration itself.
//Ex:
//Error rates = [5, 10, 15, 20, 70, 70, 65, 10, 10], threshold = 16, time = 3
//Return false
//Explain: Starting from time 3 (error rate 20), we can’t find the time frame that’s under the threshold.
// The minimum weighted error rate is 5 * 3 = 15.
//Ex2:
//Error rates = [5, 5, 5, 20, 70, 70, 65, 10, 10], threshold = 15, time = 3
//Return true
//Explain: start from time 3 (error rate 20), if we choose the error rate before it, 5,
//the result becomes 5 * 2 since we want to include the given time. This gives us 10 which is below the threshold.
//Note: 這題花了很多時間clarify，一開始沒搞懂要include given time
//第四輪coding
//感覺是個ABC
//Design a data structure that represents a hand of cards and implement a method to return the biggest flush straight.
//Note 1: 這題蠻straightforward的，不知道是不是自己太菜
//Note 2: 這輪面試官是L5
//第五輪coding
//國人大叔(由衷感謝這輪非常interactive 的interview experience)
//Design a st‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ring replacing method.
//Ex:
//x = “A”
//y = “abc/%X”
//Return “abc/A”
//這題很像Terminal裡的command alias的實現，需要考慮多個corner cases像是command重複的造成的infinite replacement,
//Note: 這題可以用String.substitute();
//
//// https://www.1point3acres.com/bbs/thread-880348-1-1.html
//
//
//59.
//三道题
//1，是一个 two pointer 给一个 array 和一个index ， 然后对这个index 两边进行two pointer
//- 体验不是很好，感觉这个大哥的题是老题🙄️， 为啥不选选新题呢。
//2， leetcode 241 就是给你一个string 和一个数，然后对string  Add Parentheses问能不能得到 这个数
//eg： String: 2+2 *3 数是12 return yes as （2+2）*3 = 12
//这题没刷过，现场强行 用calculator ii + bracktracking 做的，感觉面试官很无语.
//写calculator ii  用了20 分钟😂
//3， 药无领 - 一个简单的stack， 很多followup 比如 invalid input 然后是如何scale
//
//// https://www.1point3acres.com/bbs/thread-880267-1-1.html
//
//60.
//. 跟 https://www.1point3acres.com/bbs/thread-873959-1-1.html 里面第一道安卓题的一个意思，换了个壳，
//输出要求是有数字interval也有对应的list
//output format: [([1.0, 2.0], ["v1"]), ([3.0, 6.0], ["v1", "v2"]), ([7.0, 7.0], ["v2"])]
//2. 给一个matrix，里面每一个element都是一个字母，完成isInMatrix(matrix, word) function。follow up是给1.isWord(str) -> bool 和2.isWordPrefix(str) -> bool, 完成findAllWords(matrix) function。
//3. 最近很火的游戏Wordle。第一问给word让你返回结果，如果正确的词是"system"，你猜的是"absent"，
//则返回"RRGYRY" (R：没有这个字母, G：有这个字母且位置正确，Y：有这个字母但位置错误)。
//第二问给定第一问的function(guessWord(word) -> result)和一个巨大的包含所有六字词的wordDictionary，implement一个function，
//在6次之内猜出结果。
//4. 两个intervals有没有intersection，n个intervals有没有intersection，n个intervals共有几个intersection
//5. 给黄绿两种颜色的布，给定黄色布的位置，比如[1,3], [5,8], [9,15]，剩下所有位置都可以认为是绿色的布。
//input：黄色布的位置list，一块绿色布的长度。问：把绿色布放到任意位置，最多能剩多少黄色布。
//6. 匹配足球比赛，给定1.国家和足球队的dict（e.g. {'Italy‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌': [c1, c2], 'Spain': [c3], 'France': [c4, c5, c6]) 2. 之前踢过比赛的队[c3, c6], [c4, c2], [c5, c1]，要求匹配下一轮的n场比赛（任意一种组合），要求同一个国家的不能一起比，之前踢过比赛的队也不能一起比。
//
//// https://www.1point3acres.com/bbs/thread-879616-1-1.html
//
//
//61.
//应该是刷题王弃流其变种，不过需要空两个。印象中刷过这道题，不过年纪大了，昨天刷的今天就是它认识我我不认识它。。想不起哪里见过，
//如果不是这道题肯定是其他哪道。。听声‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌辩位应该是一国人小哥，各种提示还是不会。。哎。。
//// https://www.1point3acres.com/bbs/thread-879582-1-1.html
//
//62.
//4.4刚面的 也没准备不知道是不是旧题 简单来说就是写个func input是list of events 返回l
//ist of coordinators for rendering those events in calendar.
//
//具体input和output什么结构需要自己定义 我这么写的
//type Event struct {
//    StartTime time.Time
//    EndTiem  time.Time
//}
//type Coordinator strut {
//    Top int
//    Left int
//    Height int
//    Width int
//}
//func ScheduleEvent(events []Event) []Coordinator {
//    // sort events by event.StartTime in ascending order
//    ....
//   // loop events slice to calculate coor‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌dinator
//    ....
//}
//需要注意的就是如果两个events有overlap 每个event的width就是max width / number of overlapped events
//
//// https://www.1point3acres.com/bbs/thread-879200-1-1.html
//
//63.
//1. Historical log, 就是类似某个蠡口原题，会不断更新某个string的值，然后问你相应时间值， 要求写test
//2. 杨辉三角，二维数组和滚动数组解法3. 大数据浮点数乘法， （1） 加法  （2） 乘法
//4. behavior
//5. 逆波兰， 要求处理各种exception
//感觉总体来说面的还是比较简单。 题目都有点老，有些FOLLOW UP问的有点奇怪，没太听明白。
//想问下狗家现在的评判标准是什么。楼主通过朋友打听，2个HIRE, 2 个 LEAN HIRE, 1 strong no hire.
//那个Strong no hire我是真不明白因为啥。。。我第2问差1行就写完了但是他不让我写了，那个面试官看着也没什么问题，就是不太交流。
// 我每写一段问他，他都说你先写吧, 而且有点听不懂我讲话。(这一‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌轮真的郁闷，写了好多CODE。。。没想到那个面试官从一开始就准备给我STRONG NO HIRE）。
//
//// https://www.1point3acres.com/bbs/thread-879174-1-1.html
//
//64.
//1. 给N 张扑克牌，不同的suit 和rank组成一个输入数组， 返回最大的一组同一suit中5张连续的牌， follow up，如果suit更多怎么办。
//我的答案是建四个list，append不同的value，然后sort每个list，然后找5个连续最大值再互相比较。（maxheap 也可以）
//follow up 我用了dict， 后面一样。
//2. Tree （不是binary）， 树叶上有value， 树枝结点没有value， 但有所有子叶value 的range。
//叶的value是sorted所以不同path 的branch 没有overlap。
//给一个value，返回从root开始的path。
//题不难，DFS就可‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌以，但注意建立TreeNode的时候要分成两类，我最开始没审好题走了弯路，后来定义两个class就解决了。
//
//https://www.1point3acres.com/bbs/thread-878874-1-1.html
//
//
//65.
//https://leetcode.com/discuss/interview-question/1584207/google-onsite-gems
//
//// https://www.1point3acres.com/bbs/thread-878662-1-1.html
//
//66.
//第一轮：BQ
//    常规问题：遇到过的challenge的proj；给了一个很紧急的任务，要怎么安排；难相处的同事，等等，记不太清了
//第二轮：
//     binary tree
//    给一个tree，删除里面的leaf node，输出删除的顺序
//    follow up是，如果一个node没有任何leaf node，这个node就必须马上删除，no matter还有没有其他leaf node
//    用dfs就行
//第三轮：
//    给一个string，类似aaabbc的形式，就是相同的char都放在一起，不会有aba这种形式的，输出出现次数最多的那个char
//    用tow pointer
//    follow up是怎么优化space和time complexity
//第四轮：
//    写两个function，第一个function是插入一段interval和相应的一个operator name，判断能不能插入，
//可以的话分配一个不重复的id并且返回这个id，第二个function是用id找一个operator，如果存在这个id，就把相应interval删除，
//如果存在这个interval，删除成功，返回true，如果不存在，返回false
//    用的treemap，但是没能全写完，所以没follow up
//第五轮：
//   ‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ 给一个integer array，找到一个subsequence，在这个subsequence里，后一个element总比前一个大1，输出它的长度
//    follow up是，如果要求的是在subsequence里，后一个element比前一个大的范围是1 ~ N，给出最长的subsequence
//分到ECI Track应该是第三轮表现的不太好，本来没觉得能过的。
//
//// https://www.1point3acres.com/bbs/thread-878266-1-1.html
//
//67.
//第一轮coding，热情天竺大叔，题目是某网彝鋈輘，follow up是如何判断input valid，整体很不错
//第二轮BQ，冷漠白人小哥。
//问了一些project和teamwork的经历（项目遇到什么困难，你怎么帮团队解决的？不同工作风格的人在一起做项目的经历，leadership体现在哪里等等），
//整体不是很tricky但持有有一种judge/疑问的态度追问很多细节让人有点压力。
//
//3.给的题目是地里传纸条变体。原题是比较复杂的DP但感觉最近咕咕放水给改成一个纯math计算概率的题目了。
//计算概率公式搞清楚单纯遍历path就可以做出来，但因为抗抑郁药副作用比较大打字手抖还慢导致没写完...
//4.貌似面试官同时在处理其它工作，沟通不积极，还有几次在处理自己的事情没在听...整个气氛很僵硬，
//题目是Nary-tree的先序遍历变体并打印。一开始说回溯做但面试官表示用不着太麻烦了用递归遍历...总之思路想出来了但代码写不完，
//后面问时间复杂度的时候脑抽答了个N^N。。感觉这轮必须no hire了...
//
//5.交流合作的都很愉快。问题是某网站轜酃酃变体。第一部分给包含0、1的2d数组找boudnary pixel(满足两个条件，本身是1且上下左右邻居至少一个是0)，
//暴力法双层loop解决。第二部分找给的2D数组每个点到boundary pixel距离，BFS搜索记录距离+hashset标记可以解决。不是很难，
//思路和时间复杂度也都跟小哥讲的很清楚，但因为药物副作用的问题第二部分没有写完比较可惜。
//// https://www.1point3acres.com/bbs/thread-878219-1-1.html
//
//<HERE>
//68.
//第一轮：说有一个店， T[N] , 表示N 个服务员和 他们服务一个人的时间（每个服务员只能同时服务一个顾客）。
//店刚开门，前面有M 个人在等着，这个时候来一个新顾客， 问他要等多长时间
//第二轮：饭店排队系统
//第三轮： 拷问
//第四轮： 蠡口 流散酒， 但是不太一样，要格式化打印出每一步正在运行的函数。 当一个函数A start之后，如果没结束，再start另一个函数B，
//那么B就是A的子函数。如果再来个C，D，E类似的，这时候要打印：
//A(运行时间)
//  B(运行时间)
//    C(运行时间)
//D(运行时间)
//  E(运行时间)
//要用空格排好
//第五轮： 韩裔姐姐，全程不怎么说话。 就是给一堆坐标点，找里面面积最‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌大的一条边平行于X轴的平行四边形。
//
//第四轮是 陆叁陆
//
//// https://www.1point3acres.com/bbs/thread-877866-1-1.html
//
//69.
//第一轮： 给两个树，输出diff。 Follow up 修改第一个树变成第二个树
//第二轮： 倚洱久伞
//第三轮：设计youtube
//第四轮： 思瑶林
//第五轮：最挑战，最自豪，conflict
//
//// https://www.1point3acres.com/bbs/thread-877799-1-1.html
//
//70.
//1. 衣衣六期
//2. 衣衣斯伞 + 思思
//3. 有点像 肆刘玲，但是需要treemap
//4‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌. 给出一些时间戳让找相邻最近的两个时间，要求constant，因为一天内时间戳数量固定
//
//// https://www.1point3acres.com/bbs/thread-877541-1-1.html
//
//71.
//Q1. What is the shortest paths assignment? Brutal O(N!).
// Follow up - complexity of current implementation, how to optimize? where is the most costly part (while loop recursive)
//Q2. robot room with instruction - move, turn, stop, use a grid and visited DFS. Shortest path to charge? BFS.
//Q4. user search logging, how to implement the log
//E.g., logging file write to SQL DB, commit then write to main table
//how to handle server down (track the last file processed, and last segment in that file
//how to assign files to each server
//how to delete from memory
//Q5. N-ray Tree, find root, find the parents, find next, build shortest, max sum path, adding, merging, deleting, etc.
//Mainly testing traverse + logic coding (e.g., delete impacts the sub-tree sum, range path stored in node)
//// https://www.1point3acres.com/bbs/thread-877053-1-1.html
//
//72.
//oding 1:
//地里出现过的find all bad versions.
//给定input数组，相邻的version只能一样bad，或者右侧的version比左侧的version更bad。同时给定一个helper function，input两个不同的version number ，
//假定为v1，v2，output是两个version时候一样bad，或者v2比v1更bad（假定v1 < v2）。
//Lz先口头说了一下暴力解，然后用recursive binary search解出来了。需要注意的是recursive binary search最极端情况下时间复杂度也会达到O(n).
//Coding 2:
//给一个二叉树，每个节点都有一个value值，求二叉树中相邻的K个节点所有value可以构成的maximum sum。
//Lz的思路是dfs的同时，对于每个节点返回一个数组，数组的index对应 的值是该节点和其子树中任意节点（共index个节点）所能构成的maximum sum。
//这样只需要遍历所有节点一遍就可以得到最终结果。
//Coding 3
//刷题网 依依吴伞 变种，比原题还难，大概是给定两个长度一致的字符串，找出一个one to one mapping可以把第一个字符串转成第二个字符串。
//这题Lz到现在也不会做，但面试的时候和面试官讨论后确定这是一道图的题，实现了大部分构图+dfs的代码。
//需要注意的是形成的mapping可能造成cycle，dfs的时候要额外处理一下。虽然代码肯定是不work的，还有bug，但面试官还是抬了一手，
//可能图+dfs是on th‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌e right path
//System Design
//设计一个国税局Audit Tax Report的系统。实话实话，我自己都不知道自己怎么说满一个小时的。。。
//2周后Recruiter通知被downlevel，HC approve了L4，反馈是coding都没有问题，Design不够L5 Bar。Lz有其他厂L5 offer，遂要求加面一轮Design，
//Recruiter非常迅速的帮忙安排了。
//加面Design拆了壳子其实是Distributed File Storage。
//
//// https://www.1point3acres.com/bbs/thread-875971-1-1.html
//
//73.
//第一轮：
//已知有一个类， 有两个function可以调用， hasNext(), 和 getNext() 分别输出是否有下一个， 和得到下一个char.
//假设已知这个类的两个实例， it1，和it2， 让我们来比较两个类的大小。
//例子： it1 = 34.12345789
//            it2 = 34.12346
//如果 it1 < it2: return 1
//或者 it1 == it2: return 0
//或者 it1 > it2: return -1
//这道算是一道基本算easy的题， 做的还挺顺利， 华人大哥基本上也挺客气， 感觉很positive。
//第二轮：
//There is a ball on the nth stairs, and it wants to get to the ground(level 0).
//During odd time, you can jump down 1, 2 stairs.  During even time, you can jump down 2, 3 stairs.
//Return the number of ways you can ge‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌t to ground.
//基本上题义就是这样， 之前好像地里有类似的题，但是我在leetcode上没找到， 不知道大神知不知道这道题的题号。
//我做的时候用的DP的思路， 但是写的时候很乱， 而且面试官有点冷漠， 全场下来比较冷场，
// 场面不太对， 然后最后算是也没做出来。 比较或者非常negtive。
//
//// https://www.1point3acres.com/bbs/thread-874886-1-1.html
//
//74.
//round 1：给一个安卓app的不同version的安装包，每个安装包有不同的安卓系统要求(只考虑major version)。example
//v1: min 1.0 max 6.0
//v2: min 3.0 max 7.0
//把安卓系统从 0.0 -> latest(面试官说是MAX_INTEGER)每个区间支持的不同安卓安装包找到，然后只返回所有区间
//[0.0, 1.0] [2.0, 2.0] [3.0, 6.0] [7.0, 7.0] [8.0, latest]   -> 然后这些interval作为result
//  none         v1        v1,v2          v2          none        -> 不需要返回
//round 2：常见面经餐厅的waiting list问题。设计一个waitingList class，
//API：AddGroup(Group group), RemoveGroup(Group group), GetNextGroup(Table table).
//要跟面试官都交流探讨这里面Group和Table class都需要什么field和method。最后楼主没想到最优解LRU。因为餐厅里面最大的桌子是确定的，
//所以用了n个TreeSet。
//面试官重点问了如果用了Set要怎么override Group class的equals和hashcode
//round 3：蠡口贰舞叁变体。不让返回一个需要几间room，而是返回这些room同时开会的那个时间段。如果有好几个时间段满足 返回最长的那个
//楼主只有3coding 1bq。。。也挺奇怪的。整个面试过程面试官什么字都不会打，全程口述题目。需要自己去clarify，做笔记跟面试官确认理解的题意是否正确。
//
//// https://www.1point3acres.com/bbs/thread-873959-1-1.html
//
//75.
//早上第一轮bq 就简单聊聊 印象很深的问题是 什么是好的team 什么是好的culture
//第二轮面试官非常好 波士顿office的 寒暄一下之后给了拔四舅的套壳，follow up是要安排n个人
//第三轮是google map的面试官，给了一个matrix int[][], value代表村庄的海拔，水只能从高处流到低处，给定村庄a和b，
//请问在哪里修建水井，水井的海拔最高 对a和b分别作bfs/dfs 看看能到哪些点，都能到的点里面取最高的。
//follow up 记得不太清楚了 很确定的是加上了一个cost 用dijkstra
//第四轮 地里面很火的餐厅waiting list ood 一模一样
//第五轮 非常好的国人姐姐 开始想给餐厅的 给她说上一轮原题之后 她换了特别easy的题目..
//songs 排序 排序可以调api，聊的蛮开心 还说进去了一起吃饭来着
//
//// https://www.1point3acres.com/bbs/thread-873019-1-1.html
//
//76.
//第一题 给一个string，找最长的substring, subsutring中每个字符出现的次数是相等的
//第二题 给一个数（input）找到偶数之和为这个数，比如给6，需要 return [2, 4] or [4, 2]
//// https://www.1point3acres.com/bbs/thread-872892-1-1.html
//
//77.
//1:  伞溜硫， 找叶子
//2:  骑酒思
//3:  国人大叔的题，在一个平面上给起点，终点，
//一堆点的排列和规定距离，如果两点（a,b) 在规定的距离内就可以从a跳到b，问从起点开始，经过任何一个所给的点，能不能到达终点。
//我第一反应就是union find,大叔提醒那如果很多点全部都要连一次的话时间会不会太慢？
//我没反应过来然后就改方向去dfs找有没有可行的路径，大叔也没有放弃我继续各种提醒，
//我终于领会了大叔其实是想说只要一找到合要求的路径就行，那就是bfs了。所以感觉这轮评分可能是在中间因为要面试官的指引。
//
//题目是散令令变种，我一开头就提议用空间 O(n)的dp, 她叫我dry run了一下，我dry run了以后才发现搞错了题目的意思，
//本来是找最长顺序（可以有间隔），我写了找最长连续（没间隔的），但其实我dp的方法还是最优的只是稍微改一改就好
//（后来面试完了我自己写了一下果真就是改一点就可以了），但这时候天竺女提议我不要用dp改用暴力解，我当时就蒙了半分钟，这题太明显是用dp了，而且我自学dp的时候都是直接看最优解法所以就跳过了如何从暴力解法开始。但是既然面试官建议，我也硬着头皮去尝试，但‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌是路越走越不对劲而且天竺女完全没有想帮我的意思，自己dry run的时候就发现怎样改都是有bug，我提议用回dp但被她间接拒绝了，结果时间快到心态也崩溃了，怀疑这一轮天竺女给了我一个差评然后我就被拒绝了。
//
//// https://www.1point3acres.com/bbs/thread-872607-1-1.html
//
//78.
//而玲玲 变体
//尔雾散类似，请自行google运行网络服务器，利口讨论区有原题
//离口散柳遛 追问 必须逐层去掉叶节点
//设计api，追踪过去固定时间内数据流的最大值
//三月初面试，隔了个周末被HR通知有足够反馈送HC，今天收到HR消息说HC过了，准备team match
//
//// https://www.1point3acres.com/bbs/thread-872297-1-1.html
//
//79.
//1. 给一个树，也可以说是图，每次删除这个图上的一个叶子节点，并把跟这个叶子节点相连的父节点放到一个数组中去，
//直到这个图只剩下两个节点，就不再删除。问题是给这个数组，反向推导出图的结构。
//这个题是刷题网 散摇铃 的反向版本，但是那时候我还没有刷到。一旦想清楚怎么做之后，代码实现并不难。但当时我整个人都不好了，
//被可以用少量的信息得到更多的信息这件事震惊了我。
//2. 给一个数组，每个元素是等长的单词，问这个数组中，是否存在两个单词，其中一个可以由另外一个替换一个字母得到。数组内可以有重复的单词。
//像是 幺儿起 的简化版本，和 要陆遥 的复杂版本。我用 幺儿起 一步的做法做的，时间复杂度和空间复杂度都不小。
//后续想optimization的时候想到prefix tree和suffix tree，但讲‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌着讲着觉得prefix tree不行，得用prefix table，心
//里觉着这么复杂的代码我可写不了，有点泄气，时间到了就不了了之了。事后不抱希望，没想到可以move on。
//// https://www.1point3acres.com/bbs/thread-871556-1-1.html
//
//80.
//1. 伞药琪简单版，没有obstacle。直接用BFS写了，聊了时间复杂度，还聊了DFS跟BFS在这题的区别，简单分析DFS的表现。
//Follow up是如果这个grid很大memory放不下怎么办，基本上就是divide and conquer或者map reduce如果有很多机器。感觉这轮至少是hire
//2. 据说是面经题，面完再看地里最近好像也有人遇到过。题目是一堆timestamp，精确到分钟，比如[23:11], [10:55]...
//让找出两个时间最小差值。一开始说了sort后找，面试官问有没有别的方法，我想了一分钟冷场，面试官说ok没关系我们先写sort，
//就随便写了一个comparator。然后再问现在有没有别的想法，
//提示了一下想想"the nature of input"，我就说bucket sort，面试官说ok，然后又实现了一下。follow up是如果最小时间差是头尾(最开始clarify过，面试官说先不考虑这种情况)，就改进一下code。可能这一趴给了hint然后在follow up的时候没对上面试官电波(我说时间差是一天，所以直接24*60 - (尾 - 头)。然后他没懂，好像反复在说直接(头-尾))，自我感觉这一轮反馈mid。
//3. 系统设计，这轮反馈真的很抑郁。题目就是API rate limit，我刷书也刷过好几遍，这个也很自然按照流程讲。
//面试官在面试前说他可能会在听到他感兴趣的点的时候随时打断让我不要介意。我说ok然后就一通聊，
//聊过middleware跟daemon的比较和优缺点，聊过data怎么存储，聊过规则怎么加载。
//他对daemon这里比较深挖，重点在host之间communication。我说可以用in memory的方式，可能这里踩雷了，
//他可能想的cache，但是因为之前重点讨论过想维持low latency所以没展开说。后来讨论算法，一开始我给了token bucket，就详细展开来说，
//写了伪代码，也没要求讨论别的算法我也没提。最后问了如何解决concurrency，说了append only log。整体感觉下来真的有来有回，所以拿到“horrible”这种feedback真的很打击人，面试完我还感觉这轮是positive。
//4. DP问题，一条直线表示路，路上有很多加油站，每个加油站的油价不一样。给你一辆车，起点的时候油箱满的，问到终点的最低cost是多少，
//如果不能终点，简单处理一下。每次遇到DP都真的很小心，这题是在讨论中完成的，先给了dp状态转移方程，再详细写了一下，写的时候发现邮箱还有大小限制，所以又引入了tank size，又写了一下，在讨论加多少的时候，我把邮箱的量转化成了能开的距‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌离，可能这里也是一个他觉得code over complicated的点。完成后聊了聊时间复杂度就over了。
//5. BQ，聊的挺开心
//// https://www.1point3acres.com/bbs/thread-871236-1-1.html
//
//81.
//两轮店面一道队列模拟一道离口洱岭饵，followup‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌大概medium难度
//第二题做完+followup答完+问完面试官问题还有15min，估计是这一轮给了strong hire所以后面流程都比较快
//// https://www.1point3acres.com/bbs/thread-871038-1-1.html
//
//82.
//1. behaviour  一半时间问leadership，一半时间问tell me a time的问题。把面试官给聊high了，主动说要给我strong hire..
//2. coding:设计个数据结构，可以实现：
//    1. add a range, such as [1, 3], [4, 5] [2, 6].
//     2. query a point is in the range. for example, 0 is not in, but 2 is in based on the 3 ranges above.
//3.  https://leetcode.com/problems/binary-tree-coloring-game/
//4. https://leetcode.com/problems/interleaving-string/
//5. 设计一个抢‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌购iphone的backend. 主要考察muliple region services, redis.
//
//// https://www.1point3acres.com/bbs/thread-870974-1-1.html
//
//83.
//round 1
//这题在面经里见过，但看的时候前人并没有给出一个样板，所以我没有仔细准备到。希望给不清楚的后人一个更明确的picture。
//给了一个binary tree structure里面存有一串string, tree由两种node组成：
//内部节点和末梢节点。末梢节点上有字符串的value和长度,而非末梢节点（内部节点）只有它子节点的字符串总和长度。
//Q1让定义这两种节点，Q2给定一个index n, 让写一个function来输出字符串的第n个字符
//round2
//一个alphabet set，输出所有用这个set可以组成的string的个数，且拥有如下的性质：总长度L,同时最长的不超过k个重复字母
//round3
//给你一个list: [(user1, score1), (user2, score2) ...]，让按照list里面user本来顺序输出score排名前两位的Users，
//这里有蛮多edge cases, 比如如果同分怎么办。Follow up有点绕，如果最高score一定要出现在output的第一个怎么办？
//这个面试官一直就让我自己想怎么解决,大概是想看我的engieerning sense， 并没有想帮我简化问题...
//round 4
//是个data structure design的题，比较复杂，给了两个需要自定义的classes,
//一个是单一的TreeNode，另一个是Tree,让完成一系列functions,
//包括搭建Tree的function(def i‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌nitializeTree()), 在Tree里面给定parent node让产生一个新TreeNode的function( def createNode(parent):), 随机选择一个node（getRandomNode()), 还有随机选择一个末梢节点（getRandomLeafNode())
//
//// https://www.1point3acres.com/bbs/thread-870825-1-1.html
//
//84.
//题目很简单，给你一 二叉搜索树。
//让你实现两个function
//1. 最左边的节点。
//2. next node， 就是下个比你大的节点。
//写的有点啰嗦， 让我优化了一下， 其实就是就是make code more clean， 话说这个题dry run 好尴尬。
//follow up question
//如果没有val（）， 只知道是BST， 怎么改写function 2、
//后来我想想大概她‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌是对我的优化不太满意， 唉， 想起来都是泪，题很简单， 感觉题白刷了，就是真写的时候有点发懵， 大家平时刷题还是要落实到笔头上才好。
//
//这题是蠡口 儿吧武 吗?
//
//// https://www.1point3acres.com/bbs/thread-870676-1-1.html
//
//
//85.
//面试官上来甩了一张类似这个图, 不过里面还有多边形, 比如L或者T形状的图案.
//题目是自己设计input, 然后找出所有正方型的数量
//说好的google现在面的简单了, 没想到出了个这么难的题.
//我想的是输入是一堆长方形, 所以有左上角坐标, x,y, 然后长宽. 每一个长方型都有个自己的id, 然后生成一个2d array
//类似
//aabbc
//aabbc
//zyyqq
//qqqqq
//后来的会把前面的覆盖掉, 然后以这种方式生成多边形
//最后就sliding window穷举. 有一个小优化我能想到‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌的是, 建立一个map, 来存每个形状的面积.
//然后知道winodw的面积,与里面包含的形状的id后, 可以对比一下那些形状实际加起来是否等于这个window的面积.
//
//// https://www.1point3acres.com/bbs/thread-870576-1-1.html
//
//86.
//1. 给一个数，找到最多的偶数之和为这个数，偶数从2开始计，比如给24， 求return [2, 4, 6, 8, 12]
//2. 给一个字符串，找最长的substring, subsutring需要满足其中每个字符出现的次数是相等的
//
//第一题例子有误，input 24， retur‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌n是 [2, 4, 8, 10], 感谢沙发同学支出
//
//
//// https://www.1point3acres.com/bbs/thread-870402-1-1.html
//
//
//87.
//round 1， 计算器 + 而已儿吧
//round 2, 假设queue只有isEmpty 和poll两个method, 现在有list of queue，怎么call 最少来找出 1. 最小size 2. 最小queue 数字的sum
//round 3， 面经里有，就是文件系统什么的，记不起来了，两问，都是基本的DFS，没什么难度，自己找找吧
//round 4, BQ
//round 5, 类似OOD吧。设计饭店waitlist，
//可以把等待人放入list或者移除（这里的人要看做a list因为可能多人一起来的, 然后又空闲的桌子，根据顺序和人数来匹配，
//比如2人的桌子，找最早来的两人的，如果没有或者找一人的。。。treemap可解
//
//// https://www.1point3acres.com/bbs/thread-870326-1-1.html
//
//
//88.
//1. 白人小姐姐全程冷漠脸，没有任何介绍直接说题目   
// 定义一个纸牌游戏，数值相同的可以叠起来，或者高度相同的也可以叠起来；两个人同时采取最佳策略，问给定一个初始纸牌序列，先手能否获胜
//   比如RGGR这四张牌，下一轮可以把一个R移到另一个R上面变成高度2，或者R移到G上面叠起来，因为他们开始高度都为1，叠起来高度就变成了2
//   给出了基础解法之后接着问如何优化，打出来每个纸牌序列转成string，记录cache result,可能这一轮就挂在follow up没答好吧
//2. 一个二维矩阵，从左上到右下走的所有路径中最大值的最小值，每次方向只能向右或者向下走，lc应该有原题
//    接着拓展可以上下左右四个方向走，从dp改成pq就行了
//    第三问给定一个string的iterator，实现新的iterator,比如s='aaabbbccddd'
//   next: [a, 3]
//   next: [b, 3]
//   next: [c, 2]
//   next: [d, 3]
//3. 类似于GeoHash，给定两个数据结构Node表示GeoHash的一个位置，他可以有四个children，表示他分成四份，或者他只有一个Point表示一个饭店
//   Node 和 Point这两个类都有getDistance方法，来计算他们到某一个Point的距离，对于Node 就是表示Point到他的四条边的最短距离
//   需要实现的是给定GeoHash的root，和一个Point表示一个人当前的位置，如何返回下一个离他最近的饭店。lc没有类似的题目，Node可以看成是一个QuadTree之类的
//‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌  但是比这个复杂，在面试官的提示下写成了Pq的解法，但是可能需要提示所以反馈不太好吧
//4. System Design, 设计一个存储很多很多Json File而且需要支持update的系统，感谢国人大哥一直在引导我，最后feedback也给了支持，奈何coding不给力白费了您的力挺，虽然可能你也看不到，还是非常谢谢了。
//5. 常规的behavior question，没啥好说的
//
//// https://www.1point3acres.com/bbs/thread-870304-1-1.html
//
//89.
//1. 机器人扫房间 刷题网 司八久; 问题：房间大的时候程序哪里会最先出问题，怎么解决？
//2. 信号塔只能接收到10m距离内的别的信号塔的信息，给一堆信号塔的坐标，一个起点和一个终点信号塔，输出是否信息能从起点传到终点；
//follow up 1：只能传送给10m以内离它最近的信号塔的信息；follow up 2：假设一个信号塔接收到2个信息，第二个会cancel掉第一个（双数就不发送)
//3. 给一个最多带一个*的字符串格式，比如a*b, 再给一个字符串数组，比如["ab", "acdb", "ba"]，输出数组里面所以符合格式的字符。
//（["ab", "acdb"]是符合"a*b"的）
//4. merge interval，从最简单的merge两个interval开始，follow up到很多个interval，
//再follow up到最后生成一个两两interval之间的Adjace‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ncy matrix，
//[j] - 1 (代表两个interval直接或者间接相连)，- 0（代表两个interval不直接也不间接相连）
//
//// https://www.1point3acres.com/bbs/thread-869558-1-1.html
//
//90.
//第一轮bq。一直被打断，感觉是我话痨了😂说有三个问题要问，最后一个问题没有时间问了……可见我多话唠。
//第二轮coding偏OOD，其实很简单：给一堆图和一张画布，图有些pixel透明有些不透明，
//可以叠起来覆盖底下的，问最后叠完之后是什么样的。这个感觉就是每张图遍历一遍pixel。
//但是感觉phd大哥有更好的方法，比如他说如果画布很大图很小，怎么优化？大哥态度超级好，到时间了还说我可以跟他聊天啊，
//于是问了他phd做的项目，现在的工作。感觉聊的还挺开心，可惜估计挂了。
//第三轮coding，用暴力也没做对。感觉应该用greedy+heap？给你n个球员，每个球员有编号和weight。
//然后组队比赛，两支队伍组成一个比赛，每个队k人，要求公平竞争，即队里的球员weight要么相同，要么相近。
//最后要求返回n/（k*2）个比赛。每个队员只需要存在一个队伍里就可以了，不需要排列组合。
//第四轮coding，问了俩问题。第一个是判断一串数组里存不存在某个数，它比其他数都大k。第二题是2d数组里每个格子有weight，从左上走到右下的最大和。两个都做出来了但是‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌感觉都不是最优解……
//第五轮design。这个感觉是答得最好的了。主要是让我设计一个noSQL db。刚好最近看了dynamo paper，于是说了很多，感觉大哥也不时点头肯定。不过也是幸运……
//
//// https://www.1point3acres.com/bbs/thread-869360-1-1.html
//
//91.
//第一面问的就是1610这个题地里看见过但是没有来得及做一整个磕巴住了估计nohire了第二面bq
//第三面
//给dic{"x":"123,"y":"234"}
//然后再给一个string eg “%x%a%y%"
//return "123a234"
//follow up dic{"x":"123,"y":"%x%"}
//return become "123a123"
//第四面
//地里出现过的directory的问题就不阐述了多翻翻就有
//第五面
//假设你有一个phonenumber的存储
//实现
//1.number insert to db
//2.is number‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌ taken
//3.give a new number
//"
//// https://www.1point3acres.com/bbs/thread-869083-1-1.html
//
//
//
//92.
//第一面:有n个数,需要比几次才能选出最大值,n-1,然后怎么证明
//第二面,国人小哥,给个sorting array,返回平衡二叉树.sorting list?.follow-up,sorting list 复杂度降到O(n)  ..
//第三面,扫雷游戏,从随机生成雷开始写.最后又做了一个linkedlist 简单排序, 给一个target,小的在前,大的在后.
//第四面,记得不是很清楚了,related tf idf formula, glassdoor上有这题
//第五面,设计一个类似Uber的,但是‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌用无人车的手机app
//
//// https://www.1point3acres.com/bbs/thread-868913-1-1.html
//
//
//93.
//BQ：
//很常规，就是介绍自己的项目，介绍自己带人的经验，自己失败的project etc。
//Coding 第一题：
//不难， 给你一个string，找里面distinct char 最长的substring，返回长度。例如：input： “ABCDAWERTY”,
//里面 distinct char 最长的substring 是：“AWERTY”， 所以返回长度6.我用two pointer做的，后面问了follow up，
//如果这个input 特别长，一个电脑放不下怎么处理
//coding 第二题：
//给你一个string list， 里面每个元素都是一个timestamp，格式是： “hh：mm”， 例如： ["23:23","11:13","21:01","01:03"],
//返回其中两个元素最小的时间间隔。我说先sort 再做，面试官希望用O(N)的时间复杂度。没想出来
//coding 第三题， 有点复杂，上图：
//真心不会做。面完这轮就知道铁定挂了。
//System design轮：
//设计一个data center 的machine monitor system。完全没有准备过类似的题目，而且面试官恰好就是做这个data center 之间的网络传输的，问的很深入，答的也很一般。
//最后过了一周，HR 通知挂了，说sytem design 答的一般，但是降级到L4，coding 也没有meet bar，就no hire了。 move on。为了方便大家看，就不设置积分查看了。
//
//第一题例子错了，最长distinct char substring 应该是“BCDAWERTY”
//// https://www.1point3acres.com/bbs/thread-868755-1-1.html
//
//
//94.
//电面：
//// Given an N-ary tree, find the node that has the shortest total distance to all other nodes.
//// You may define the tree node class anyway you want. The input to the function is the root node.
//第一轮：利口 巴腰罢
//第二轮：
//Imagine an office which issues license plates, in the following format and order:
//00000
//00001
//...
//99999
//0000A
//0001A
//...
//9999Z
//000AA
//...
//ZZZZZ
//Write code that returns the Nth license plate in this sequence, given N as the input.
//第三轮：
//// Design a class organization
//setManager(manager, reportee) => 将reportee的直属上司设为manager
//setPeer(personA, personB) => 把personA和personB设为peer，代表着这两人将有相同的上司
//reportsTo(personA, personB) => 给定两个人，返回personB是否是personA的上司，注意不一定是直属上司
//Example:
//setManager(A, B)
//setPeer(E, D)
//setManager(B, D)
//reportsTo(E, A) => true
//第四轮：
//Given a binary image, with pixel value equal to 0 (background) or 1 (foreground), find the boundary pixels.
//A pixel is a boundary pixel if
//1) It's foreground (pixel value = 1) AND
//2) It has at least 1 background pixel among its 4 ‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌neighbors (up, bottom, left, right)
//   If the pixel doesn't have 4 neighbors(on image edge), it is a boundary pixel.
//Q2: Compute the distance transform (DT) of this binary image.
//For each pixel, the DT is equal to the distance to its nearest boundary pixel.
//Here the distance is defined as |row1-row2| + |col1-col2| (Manhattan distance)
//这题类似利口 尔拔榴
//
//第五轮BQ，聊了很多简历上的项目，此外还问了如何化解跟同事的conflict，与人合作的经验，遇到过的困难，等等。
//
//// https://www.1point3acres.com/bbs/thread-867992-1-1.html
//
//95.
//蠡口 药尔酒伞
//
//国人小哥超级nice，感觉在给我放水。 题目是狗家高频 变种，即原题在只能消灭一个障碍物k=1时的情况。我还是用的三维visited数组，小哥应该是follow-up让我用迪杰斯特拉 那种dp做法，然后又问了下dfs怎么实现。对于这种直接考高频的，什么情况，不用我多说大家也都懂吧。
//
//// https://www.1point3acres.com/bbs/thread-867816-1-1.html
//
//
//96.
//店面掛了
//老實說我很驚訝 題目很簡單
//給一個 2D array 裡面好幾個 string
//ex [['i', 'like','meat'], ['i', 'like','food'], ['i', 'am','human']]
//寫個 prediction function to predict the next words based on most frequent showing bigram
//in this case, if the input it's "i", we should return "like" cause like came ou‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌t twice but am only one'
//
//// https://www.1point3acres.com/bbs/thread-867616-1-1.html
//
//97.
//第一题：Interval。
//Given: Timestamp (ms) url status duration (ms)
//0001           xx              200    150
//0002           yy              500    1
//0005           zz               200   20
//1000           ww            200   10
//Ask: Finding the number of concurrent request at each timestamp
//Sample result:
//0001 -> 1
//0002 -> 2
//0005 -> 2
//1000 -> 1
//
//第二题：Replace substitution in a string (Directed Graph)
//Input 1:
//FIRSTNAME --> Bill
//LASTNAME --> Gates
//FULLNAME --> %FIRSTNAME% %LASTNAME%
//Output1: This is %FULLNAME% --> This is Bill Gates
//Input 2: invalid input because there is a cycle
//ONE --> %TWO%
//TWO --> %THREE%
//THREE --> %ONE%
//
//第三题：蠡口二零零
//第四题：Max profit for growing crops，给定几种庄稼的种‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌类，grow time，profit；
//给定一块地，一次只能种一种庄稼，给定一个市场，问如何最大化收益。
//第五题：BQ
//
//// https://www.1point3acres.com/bbs/thread-867204-1-1.html
//
//
//98.
//题非常非常简单，给一个list of time（格式是HH:MM），找到任意两个时间之间的最小值。
//我上来很自然的就用堆排序做了，time complexity答了O（nlgn），但是漏了一个情况是第一个时间和最后一个时间对比，
//因为题意场景是说这是每天循环的而且不在意是不是同一天内。加上之后interviewer问我在不在意空间的情况下能不能小于这个时间，我就想了半天不需要排序的算法，最后又一番提醒才明白是因为O(24*60) = O(1)，所以应该判断如果input size > 1440 直接返回0，所以时间空间都是O(1)。
//虽然确实是我疏忽了吧，但是用这个方式去考核这个点总归是挺别扭的，因为感觉这个点更像是一个early termination或者是large amount of data的考核点，而不是一个时间复杂度的考核‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌点，当然也可能是我太菜。
//
//// https://www.1point3acres.com/bbs/thread-866925-1-1.html
//
//99.
//类似蠡口耳伞伞
//
//100.
//VO一上来我就懵了，是我太菜了，不抱希望了，moveon。
//1. given unigrams string list [new, york, hampshire, french], and bigrams string list [new york, new hampshire],
//find non match unigrams in bigrams, this example we return [french]. follow up, if no space in bigrams e.g.
//[newyork, newhampshire], I think about trie, but could not talk through clearly.
//2. find point in list of intervals, if multiple queries, can we define a more generic function
//find<T,R>(T[] list, R point)
//3. given list of strs of anagrams, find how many group we can form,
// a group is a list of words which one of them is k difference from the other.
//4. given string "%a%", % is special char for mapping and a hashmap {a-abc}, return substitute‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌d string "abc", many corner cases, and nested situations, need a lot of communication and scoping.
//
//// https://www.1point3acres.com/bbs/thread-866291-1-1.html
//
//
//101.
//上来直接开始做题
//给了一个n x n 的matrix with unique Integer,
//
//求sequence of swap得出一个sorted matrix.
//e.g:
//[[1,3,2],             [[1,2,3],
//[4,5,9],    =>     [4,5,6],
//[7,8,6]]              [7,8,9]]
//需要自己clarify什么样的output,讨论之后说是要每一步的swapped Integer, 用上面的例子就是 [[2,3],[6,9]]
//一开始楼主没想出来要用bfs，给了2种暴力解。还好只是写了psuedocode, 后面面试官提示才知道是有关搜索的题。
//因为前面clarify的时间比较长加上代码量比较多，用了一两个没有define的abstract function,只是写了写documentation (input, output, 以及一些要注意的事项, e.g check boundary等等）
//最后基本上是把45分钟全用完了，然后到QA环节，这里吐槽一下面试官。。我问了下基本的tech stack和他所在的组，
//问什么都说"我不知道自己能不能说blah blah blah"，那我也没法问下去，他说谢‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌谢和狗家面试，说到一半直接就挂电话了。
//
//每一次只能和相邻的数字swap.
//
//
//// https://www.1point3acres.com/bbs/thread-865103-1-1.html
//
//
//102.
//被hr 勾搭毫无准备就开启了我的面试之旅，整体感觉比几年前面试要简单一些，但也是竞争非常激烈
//第一轮，从数组中找出k个数，使得k个数组成的数最大，这k个数要保持原来的顺序
//第二轮，给定一个有序的数组和一个数字k，表示这个数组中有k个数是乱序的，还原这个数组 （感觉面试官希望一个小于nlogn的解，一直没get到点，不知道怎么得到最优解）
//第三轮，不含重复元素的一维数组做压缩编码，使得最新的数字为1，返回压缩后的数组，压缩后的数组中每个元素的位置应与压缩前对应 followup，包含重复元素
//第四轮，给一个机器人和一个地板，机器人需要从入口到达出口，机器人可以向上下左右四个方向移动，地板的颜色会指导机器人的移动，地板会有8种颜色，机器人每遇到一个颜色就会按这个颜色指‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌定的方向走，直到遇到其他颜色的地板，请给出一种机器人可以到达出口的方案，这个方案是指什么颜色指的是哪个方向
//第五轮，聊天
//
//// https://www.1point3acres.com/bbs/thread-864972-1-1.html
//
//103.
//有點像 利口  山烏酒 （或山流耳? ）
//要數連接。 只有一個函數“能不能連接”，不 take timestamp. 要用系統的時間。
//超過五百個‍‍‌‍‌‍‌‌‌‌‌‌‌‍‍‌‌連接後就不讓再接了。每個連接一秒鐘後自動掛斷
//
//// https://www.1point3acres.com/bbs/thread-863829-1-1.html
