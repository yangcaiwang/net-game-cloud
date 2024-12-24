# cloud-game
高性能的微服务框架

## 平台简介

~~~
* 基于SpringBoot SpringCloud & Alibaba
* 注册中心,配置中心Nacos,统一认证授权使用Security,Jwt,Redis
* 微服务之间采用grpc异步调用
* 流量控制框架Sentinel,分布式事务Seata,日志管理Elasticsearch,Kibana,Logstash
* 封装了常用的工具类,全局异常处理,多数据源
* 搭建的一套分布式微服务架构,代码干净整洁,注释清晰,适合新项目开发
~~~

## 目录结构

~~~
cloud-game
├── cloud-business    --业务服务
│       └── cloud-app      --游戏大厅
├── cloud-gateway       --网关服务 [9000]
├── cloud-modules       --通用模块
│       └── cloud-common          --公共工具
│       └── cloud-datasource      --多数据源
│       └── cloud-log             --日志记录
│       └── cloud-redis           --redis缓存
│       └── cloud-rabbitmq        --rabbitmq消息队列
├── cloud-elk        --elk日志管理
│       └── ai-cloud-elasticsearch   --存储日志数据
│       └── ai-cloud-kibana          --收集日志   
│       └── ai-cloud-logstash        --显示日志数据
├──pom.xml              --公共依赖
~~~


- 网关，访问地址：http://127.0.0.1:9000

- 查看注册中心注册服务信息，访问地址：http://127.0.0.1:8848

- 查看Sentinel流量控制台，访问地址：http://127.0.0.1:8718

- 日志收集系统信息，访问地址：http://127.0.0.1:5601

## sql表结构
