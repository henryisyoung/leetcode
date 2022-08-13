https://www.1point3acres.com/bbs/thread-792062-1-1.html

system design 是做一个search engine，要特别强调scalable
   Example inverted index:
   Cat =>
   [1, 5, 6, 9, 10, 12, 16, 19, 22...]
   Dog =>
   [3, 5, 7, 9, 16, 17, 18, 19, 21...]
   Elephant =>
   [2, 6, 7, 9, 16, 19, 21, 25, 27...]
   (Postings lists in this inverted index are always sorted)
   Given an input of search terms (eg: [Cat, Dog]), and an inverted index (like above), return all document ids that contain all of the search terms (eg: Cat, Dog => [5, 9, 16, 19...])

- follow-up
What sharding strategies can we use to distribute the inverted index over multiple Index Servers?
How might we compare these different sharding strategies?
- Recommendation
![img.png](img.png)

---
https://www.1point3acres.com/bbs/thread-777387-1-1.html
第一道design comments，考点：

1）新增@mention的功能，前端在用户输入的时候会发生什么？就是打了@会有tooltip，如何实现

2）database里如何存 @mention的内容，如果有@XXX但XXX不是人名怎么区分

3）comments可以支持rich text，就是加粗下划线这些，如何在database中存储？这个我想的是用特殊符号mark，之后parse，但感觉并不是面试官想要的答案，我也不知道还能怎么存。

4）post API里具体发送什么内容，比如“Hello @Walard”发送给backend的时候是什么形式，存在database中又是什么形式，之后get api又要如何处理。因为可能有很多人叫Walard，需要考虑如何存unique user id的信息。我答的是前端post的时候就直接改成“Hello @user_id_xxxx”，面试官一脸困惑。。。。

5）需要支持不同版本的API，database上需要什么更改吗：就是说有的用户版本可以@，有的不行，如何区分。我答用api version区分，database不用变动，gg

数据库 API的设计。讨论了很多backward compatible， support new/old/different versions of clients。 这轮知识比较comfortable，答得也还行，positive。

---

第二道题design column formula。如果说上一道题是偏product/API design，也还算常规只是我自己没准备好，那这一轮问的就更逗了。again，可能真的是我太菜了，真是没明白该怎么弄。。。。

1）如何在database中存储formula column的内容：比如C3 = C1+C2

2）什么时候触发计算？同时计算5000行如何优化

3）支持类似编程式的formula，比如where/if，如何存储，如何计算：一直被问如何从一个string “if C1 = 0， C3 = 100”计算出实际的结果。我答parse这个string，类似LC calculator解法，提取关键词if/where这些，被否定说太难了，问我有没有什么intermediate的办法，我答不上来，完全没get他的意思。

4）如何解决column之间的dependency：见下图，subtotal变了，tax和total都会变，但需要保证subtotal变之后，tax先变，total再变，如何实现？我真的不知道。。。。

6. Product design : airtable的table有很多column，给一个list of column和他们的value，还有一个list of computed column和他们计算方法，如何实现computed column这个功能，没啥技术含量。