第一轮 ： 系统设计： 设计google news， 给你500 个news agent，
1） 需要去crawling 这些agent 的website，
2）不同用户有不同的preference， 需要按照不同用户的preference 推荐news。
记得讨论了 ： web crawler， cold start， urgent news， pre-compute user‘s news feed， filtering， ranking（popular news）

设计一个新闻的news feeding系统，从不同的resource读取新闻，然后聚合推送给用户。用户也可以搜自己想要的新闻。

---

系统设计： design Instagram

news feed, 重点问了pull和push怎么设计， DB schema，以及怎么解决Hot user. 交流比较重要，然后基本上是自己drive, 对方会对应的问问题。


---

系统设计： design user tracking event system. 10B event per day. 1k event type. support api get(start, end, topK) 返回时间断内topK event type count. need to design event type, handle gdpr, data replication,partition etc.

--- 

slack

---
设计一个event tracking/triggering/monitoring system，除了基本的event store和real time analysis 还问道了event trigger。比如event A成功了 trigger B, 失败了 trigger C, B ,C 都trigger D。这个要怎么做到。半天没看懂，这不就写代码里面if else就好了嘛？沟通半天没啥回应，问他要hint来了句I know a lot of ways to do that，心里火气顿时上来了。就直接说这个request不会做，丢了个lambda架构的monitoring system讲完结束了这轮。