//rate limiter ， 算法这边我觉得说出常用的window， sliding window， bucket 基本就差不多了。然后还是聊架构。算是比较常规吧，分享几个我觉得比较值得注意的点：
//1 - bucket怎么refill，什么时候refill，为什么。如果是多个server host 怎么refill， server之间怎么交流。
//不同的data center又要如何处理？ server之间的交流频率又是什么。为什么。
//2 - 事先的clarify，题目就一行。所以先聊requirement，再说细节。 我问问题的时候，对面就开始满意笑了。
//尤其问到distributed system，对面就很开心明显。我至少问了10个问题，面试官都记录在doc上了。
//3 - 每一个系统的细节。比如load balancer，为啥要有LB，会不会是single point of failure，如果是的话，怎么解决。（其他layer类似的问题都会过一遍）

//第二轮 System design
//设计一个rate limiter （没错，和第一题一样，只是是系统设计题）

//题目很新奇，在GCP已有object系统基础上（增删改查），实现object 的compose。
//功能和这个类似：https://cloud.google.com/storage/docs/gsutil/commands/compose
//我问考官具体的use case，考官说不用考虑use case，可以抽象成各种类型文件content bit连起来就好。
//我之前没有搞过object storage，只了解一些基本知识。
//所以提了俩方案：
//1. deep copy文件然后compose，更新metadata DB。write slow， read quick
//2. 创建一个索引表，用户读取的时候按照索引iterate 提取子object，可能出现多层嵌套。write quick， read slow
//为了优化上面方案这个，参考了WAL逻辑，先create metadata更新+索引，之后worker在去post copy合并，
//这样如果用户不是立刻读取，那么write quick，read quick。
//我也提出讲了object 文件用chunk存储的，也可以考虑chunk层做索引。
//我只收集了functional requirement。 no-functional requirement, estimate, api, Data 都不需要我说，全程问我算法怎么设计，
//压缩or加密文件怎么处理。这题答到最后不知道考官想问什么。

//// 1. 设计一个温度tracker，只保留24小时温度。需要加温度和查询最大问题

//给全世界的用户在每天的localtime 8pm，个性化推送top 10新闻。正好遇上我的强项，面试官表示我说的方案和他们线上在用的基本一致。

//System Design: Design an online turn-based game.
//给个思路, 先design online game system, including user service(using SQL due to Consistency over Availability),
//game matching service(with NoSQL due to Availability over Consistency),
//and game services(with Redis due to scale and super low latency. 使用Mast/slave redundancy to prevent Single Point of Failure).
//然后Game Service API设计再加入"turn-based"元素的APIs.
//Push server and Client选Web Sockets connection (v.s. Long Polling)

//SD： 设计slack，参考设计facebook messenger, 一个意思

//第四轮 SD：设计chrome的phishing detection功能，假设ML team可以提供所有phishing url。
//
//// ---------------
//
//设计一个可以fetch top k frequent log的系统，就给这一句话，细节都得自己问
//
//设计一个logging service，题目很直球，但是考了很多 system design 的东西， 比‌如 proxy, api gateway, queue, 什么的
//
//System Design, 面试官又没准时，不过5分钟以内上了，好像是因为办公室设备处问题了，就算他准时吧。面试官来自GCP。
//问的比较底层的系统设计问题。人很nice很有耐心，问的问题都很constructive， 都是为了最终优化解而问。
//问题没有提前准备过，所以都是现场思考，优化。最后给出的结果面试官很满意，并且肯定性的说了一句google就是用的这种架构。
//当时还是蛮欣慰的，几年的工作经验和各种conferences还是没白去。体验很好。
//
//设计一个公司内部API quota enforcement系统 限制各个API的QPS，resource的QPS，user的QPS
//
//第四轮，design，设计了一个油管选500个中奖观众的系统。
//
//第三轮：设计youtube
//
//design a global counter incrementer
//
//
//设计一个国税局Audit Tax Report的系统。实话实话，我自己都不知道自己怎么说满一个小时的。。。
//
//加面Design拆了壳子其实是Distributed File Storage。
//
//题目就是API rate limit，我刷书也刷过好几遍，这个也很自然按照流程讲。面试官在面试前说他可能会在听到他感兴趣的点的时候随时打断让我不要介意。
//我说ok然后就一通聊，聊过middleware跟daemon的比较和优缺点，聊过data怎么存储，聊过规则怎么加载。他对daemon这里比较深挖，
//重点在host之间communication。我说可以用in memory的方式，可能这里踩雷了，他可能想的cache，
//但是因为之前重点讨论过想维持low latency所以没展开说。
//后来讨论算法，一开始我给了token bucket，就详细展开来说，写了伪代码，也没要求讨论别的算法我也没提。
//最后问了如何解决concurrency，说了append only log。整体感觉下来真的有来有回，所以拿到“horrible”这种feedback真的很打击人，面试完我还感觉这轮是positive。
//
//设计一个抢‍‌购iphone的backend. 主要考察multiple region services, redis
//
//设计一个存储很多很多Json File而且需要支持update的系统，感谢国人大哥一直在引导我，最后feedback也给了支持，奈何coding不给力白费了您的力挺，
//虽然可能你也看不到，还是非常谢谢了。
//
//第五轮design。这个感觉是答得最好的了。主要是让我设计一个noSQL db。刚好最近看了dynamo paper，于是说了很多，感觉大哥也不时点头肯定。不过也是幸运……
//
//第五面,设计一个类似Uber的,但是‌用无人车的手机app
//
//设计一个data center 的machine monitor system。完全没有准备过类似的题目，而且面试官恰好就是做这个data center 之间的网络传输的，问的很深入，答的也很一般。
//
//You own a network and design a system to manage resources distributed to users.
//You need to control the traffic and limit the bandwidth of upload/download according to the users'' plan
//(like the premium plan will have 10GB/s) 类似于，运营商提供不同的plan，用户购买了每个plan，有两个限制（1. 速度，2. 总量）
//1. handle how to control in one location and multiple locations
//2. multiple users in one location
//3. consider multiple devices for one user
//4. how to broadcast among different locations (User A has two devices using diff gateway)
//5. what data to record in the gateway
//6. how to manage the traffic in one gateway, how its bandwidth be distributed
//
//
//
