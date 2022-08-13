
System design，类似于design steam的上线，隐身系统。类似于实现steam或者qq上线提醒，隐身，显示上次上线是啥时候这些功能。

---

https://www.1point3acres.com/bbs/forum.php?mod=redirect&goto=findpost&ptid=905747&pid=17307999

1.你有10+T 的数据， 你有1T 的磁盘和8个cpu的资源。 怎么取出最热门的100 条数据。- 挣扎了一下， 答了出来（sharding， offline 处理）

2. 每个国家地区有一些ip 是被禁止的，要求设计一个功能能拦截来自这些ip 的访问。
有个政府网站能提供禁止的ip（面试官后来要求侧重分析数据怎么分析，并生成能快速查询的cache data。 
high-level 完了后，我一开始侧重在设计一个global分布式的API + cache system， 虽然也是在设计这个系统，但应该及早去揣摩面试官的侧重点。） - 总体沟通不畅
```
1. 可以简单得把10T的数据当成是key-value 的格式。 找出这里面出现频次最高的100 个key。 可以重复读取， 这不是个online 的service。 这些数据是乱序的。 我当时答的就是首先根据key shard 成若干分，每份用一个key-value store。 当然如果某个range 是skewed，数据量超出了存储范围就需要shutdown 其他range ，然后把磁盘空间加上去。 重复这个过程，直到算出所有range 的top 100.
2. 没有， 面试官也没要求数据结构， 但我觉得你说的是很好的思考方向。 面试官要求cache 里包含blacklist以及whitelist ， 这样可以提高cache 访问速度， 降低miss.
```
---
4. Design，在内存1GB的机器处理6TB日志文件，设计一个API返回top K的日志
https://www.1point3acres.com/bbs/thread-903748-1-1.html
---
VO1：设计Image Search。不需要关心index，但是比较在乎怎么存储和query thumbnail。
Distributed System的基础知识问的非常细，每提到一个关键词就会挖的很深，所以不太能混，内功DDIA还是要好好消化。比如你说要Replication，然后面试官会挖用哪种策略，然后为什么再这个场景下要用这种策略，还有哪些可选策略，和其他策略比较pros cons是啥之类的
https://www.1point3acres.com/bbs/thread-903618-1-1.html
---
系统设计，设计一个系统，从一个cloud storage 同步所有文件都另一个cloud storage。题就是这样的vague。然后就是基本是楼主说一些脑补的assumption，然后和面试官确认一下，面试官说ok，然后楼主继续往下。系统设计书里的套路基本没用上。中途感觉有点变成了OOD。