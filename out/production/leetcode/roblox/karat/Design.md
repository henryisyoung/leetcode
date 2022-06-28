We are designing the public HTTP API for a web service that helps corporate social media managers post to multiple social media networks at once (such as Facebook, Twitter, or LinkedIn). Our product has a few use cases:
1. Create a new post on one social network
2. Create a new post across multiple social networks
3. Provide statistics to show how users interacted with a post
4. Delete an existing post
What should be the methods of our API? Give each method a name, and specify the fields of its request and response messages.
For now, we'll assume that your API works 100% of the time and never encounters an error.
Write your answers like this sample API method - note, you do not need to address specific GET/POST/DELETE semantics:
Method: GetShippingStatus
Request fields:
Tracking number (string)
Response fields:
Status message (string)
Expected delivery date (date)
5. 每个API如果有error会是什么样的error，应该怎么处理
   
https://www.1point3acres.com/bbs/thread-805233-1-1.html