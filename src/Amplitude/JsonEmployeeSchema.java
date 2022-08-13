package Amplitude;

/*
https://www.1point3acres.com/bbs/thread-882556-1-1.html
https://www.1point3acres.com/bbs/thread-896770-1-1.html
https://www.1point3acres.com/bbs/thread-766595-1-1.html


给定一个employee schema和一个list检査list里的内容有没有符合schema
例如schema是
{
  employee: [
    {name: 'id', required: true, type: 'number'},
    {name: 'name', required: true, type: 'string'},
    {name: 'title', required: true, type: 'string'},
    {name: 'salary', required: true, type: 'number'},
    {name: 'age', required: false, type: 'number'},
    {name: 'reports', required: false, type: 'array:employee'},
  ]
};
list是
[
{
 "id": 1,
 "name": "alice",
 "title": "ceo",
  "salary": 1,
  "age": 3,
  "reports": [
   {
     "id": 2,
     "name": "bob",
     "title": "cfo",
     "salary": 1,
     "reports": [
          {
            id": 3,
            "name": "charlie",
            "title": "controller",
            "salary": 100,
            "reports": []
          }
         ],
       },
      ]
    },
]
这样就有符合
因爲有array:employee所以会有report chain
店面时问了几个follow up:
1. 不能有circular reporting chain
2. 如果没有id可以做爲identifier
3. 可以有两个老板但一样不能有circular reporting chain

 */
public class JsonEmployeeSchema {
}
