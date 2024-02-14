仿微信项目 旨在练手

技术栈

| 前端         | 后端                 | 数据库 | 中间件 |        
| ------------ | -------------------- | ------ | ------ |
| Vue          | spring-gateway       | mysql  | rocketMq|
| element-plus | spring-boot          | redis  | nacos |
|              | dubbo                | ipfs   |       |
|              | websocket            | mongodb|       |
--------
暂时启动策略（后续写为docker-compose脚本）
前端启动 npm run dev
后端 先启动user模块 不然会报找不到dubbo服务错误 
--------
release-0.0.1
目前功能： 
  1. 登录注册
  2. 群聊/私聊功能
  3. 好友功能
  4. 通知（消息 朋友/群聊申请 朋友圈发布/点赞/评论 ）功能
  5. 朋友圈（发布朋友圈/评论/点赞/回复）功能
--------
TODO
-------
1.加入seata来实现分布式事务
