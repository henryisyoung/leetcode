系统设计是 设计一个给朋友分享照片 和video 的系统 ,一开始就是一台机器, 后边没时间扩展了 ,但是面试官强调如果有时间会问分布式怎么做

设计photo sharing chat：类似于messenger，但只能share photo不能发文字，先从很少的用户开始design然后着重考察了million级别的active user时的scalability问题。

第一轮：SD，设计一个跟朋友分享照片和视频的系统。这个题目很多面筋都提到过，但是都没有特别清楚。这个题的要求是设计一个在一个周末完成的系统，所以不需要任何很复杂的设计，怎么简单怎么来。分享的方式是类似IG，发个状态给朋友看。会很详细的问到选择什么技术栈，从前端到数据库。然后还问题如何设置照片视频的访问权限。

---
design a youtube click counter

---
设计一个checklist application，可以允许多人share并协同工作，需要支持：添加list，添加item，删除item，标记完成，分享list。考察的比较细致，data model, api design, protocol，scalability都有考察到，甚至还写了点sql
这轮答的一般，特别是sharing那里的mechanism没有设计的太好不太efficient，也是为啥最后多了一轮加面。

Design a collaborative to-do list application. 大体要求是多人可以在同一个to-do list里面add task，或者complete task。其次是不同人有不同的role，从基础的viewer（可以增加或者完成task），到editor（可以改task名字什么的），到admin（可以创建删除todo list之类的）。讨论内容有API， data model， database选择，如何把某个client的改动及时同步到别的clients里面，一个todo list里面collaborator太多读写流量太大怎么办之类的。

---
设计一个delay transaction system，背景是他们自己的game platform里面的虚拟货币，要求用户可以设定一个未来的时间来给另一个用户转特定数额的钱，并可以支持cancel还未发生的transactions，可以assume有一个banking API可以call去真正执行transaction。

第二轮：SD，设计一个payment hold的系统。这个其他面筋也提到过，讲一下其他面筋都没有提到的问题。第一：QPS 5K， 第二：这个系统需要直接cancel payment。这个就需要确定payment hold API的返回值是什么，如果用了卡夫卡做缓冲会怎么样，NO SQL怎么做sharding等等，这个地方我答的不太好，很可能就是挂在这里，希望可以帮到后来人。另外也问了一下payment system怎么跟payment hold配合工作的，需要讲一下payment system的workflow和DB。

这个是面经题：Design a payment scheduling system. 主要要求是可以允许Roblox玩家说给另一个玩家转一定数量的游戏币，不过要在指定是时间点转（比如3天后，2小时后之类的），然后不用考虑payment system本身的设计，就假设有个现成的payment API。大方向上像个delayed job scheduling system，不过有一些特别的细节：（1）发起转账的用户里面的钱得立马扣下；（2）指定的未来时间可以是10几秒之后，所以定时polling的系统是不行的；（3）发给接收方的钱的操作可能会有不可重试的错误，比如接收方账户因为用外挂被注销了什么的，所以其实整个系统其实是个transaction系统，要考虑rollback，以及常见的WAL之类的手段来达到durability要求。面试官还粗略问了一些API design（REST vs RPC），API security，notification，idempotency（payment）相关的内容。

---
Design：如何migrate一个cache database。背景是比如目前用的是Memcached，需要换成用redis，你会如何进行migration。应该是因为我面的是个做database的组所以才会有这道题。

---
like/dislike counts: Design like count for Roblox Platform Item. 实现1. 一个user可以看到自己所有的like的item，1M QPS。2. 一个user可以like/unlike item，100k QPS。3. 可以看到每个item的like count，1M QPS。主要focus在怎么保证high qps，like count可以eventual consistency, 实现一个和二需要怎么保证read after write consistent。还有各种数据库的设计，哪里需要放cache等等。

---
经典的游戏评分系统，竟然还mock了UI，走心了。需要支持的qps很高. 需要支持比较高的读写qps，但是没有consistency的要求，所以partition，replication，cache啥的都可以上.

---
设计游戏匹配系统，有很多很多游戏要匹配. 要考虑系统能scale到匹配千百个游戏，像能够处理burst of data的这种消息队列要加上。要考虑region，server clusters是在不同region的。还要讨论server怎么跟client连接，这个回路也要讨论