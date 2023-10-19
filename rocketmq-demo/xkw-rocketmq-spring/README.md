# 学科网RocketMQ组件

与本地消息表集成，提供快速接入和维护本地消息表的能力。

## 2.x版本

| 基础组件         | 版本   | 链接                                                 |
|-----------------|-------|-----------------------------------------------------|
| RocketMQ        | 4.7.1 | [Github](https://github.com/apache/rocketmq)        |
| rocketmq-spring | 2.1.1 | [Github](https://github.com/apache/rocketmq-spring) |

>以上是官方对应版本，RocketMQ从4.6.1开始支持namespace，但是rocketmq-spring从2.2.2开始才支持，这个版本是为RocketMQ-4.9.3开发的。 公司的RocketMQ集群版本为4.7.1，所以修改了rocketmq-spring-2.1.1版本少量代码，以支持namespace，参考[NOTICE](./override-rocketmq-spring-all/NOTICE)。**请勿引入原版rocketmq-spring组件**。

**千万不要导入官方starter，会冲突，如有需要导入修改过的包**

```xml
<!-- 对应官方版本：2.1.1 -->
<dependency>
    <groupId>com.xkw.bcom</groupId>
    <artifactId>override-rocketmq-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.xkw.bcom</groupId>
    <artifactId>override-rocketmq-spring-boot</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 模块说明

```
xkw-rocketmq-spring
├── override-rocketmq-spring-all ······· 修改rocketmq-spring源码，已经在自研组件中引入
├── xkw-rocketmq-spring-boot-starter ··· 自动装配
├── xkw-rocketmq-spring-core ··········· 组件的核心业务模块，包括消息发送、抽象监听器、消息表的操作、定时任务等
└── xkw-rocketmq-spring-samples ········ 生产和消费样例
```

## 使用说明

### 导入sql

按需导入sql文件，生产者：[mysql.innodb.producer.sql](./xkw-rocketmq-spring-core/src/main/resources/schema/mysql.innodb.producer.sql)，消费者：[mysql.innodb.consumer.sql](./xkw-rocketmq-spring-core/src/main/resources/schema/mysql.innodb.consumer.sql)

如果升级组件版本，请参考[消息表升级说明](./xkw-rocketmq-spring-core/src/main/resources/schema/patch/README.md)

目前只支持Mysql-Innodb数据库，如有其他需求，请将生产者或消费者对应的data-source-type配置为other，并自行实现[repository](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/repository)包下的接口

### 导入依赖

```xml
<dependency>
    <groupId>com.xkw.bcom</groupId>
    <artifactId>xkw-rocketmq-spring-boot-starter</artifactId>
    <version>${xkw-rocketmq-spring.version}</version>
</dependency>
```

### 配置

下面给出完整的配置属性及默认值，属性释义请参考源码[XkwRocketmqProperties](./xkw-rocketmq-spring-boot-starter/src/main/java/com/xkw/bcom/rocketmq/autoconfigure/properties/XkwRocketmqProperties.java)

```yaml
xkw-rocketmq:
  producer:
    enable: true
    key-prefix: ${rocketmq.producer.group}
    data-source-type: mysql_innodb
    message-overdue-days: 7
    clean-overdue-message-expire-millis: 600000
    max-retry-times: 3
    scan-message-expire-millis: 600000
    scan-start-delay: 60
    thread-pool:
      core-pool-size: 逻辑cpu数量*2
      maximum-pool-size: 逻辑cpu数量*2
      keep-alive-time: 3600000
      queue-size: 1024
  consumer:
    enable: true
    data-source-type: mysql_innodb
    message-overdue-days: 7
    clean-overdue-message-expire-millis: 600000
```

另外还需要配置rocketmq，下面给出简要配置，完整配置和属性释义请参考源码[RocketMQProperties](./override-rocketmq-spring-all/override-rocketmq-spring-boot/src/main/java/org/apache/rocketmq/spring/autoconfigure/RocketMQProperties.java)

```yaml
rocketmq:
  name-server: ip:port
  producer:
    group: 建议用服务名
    namespace: 开发环境建议用姓名全拼
    access-key: access-key
    secret-key: secret-key
  consumer:
    namespace: 开发环境建议用姓名全拼
    access-key: access-key
    secret-key: secret-key
```

### 主要组件介绍

[XkwMessageSender](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/producer/XkwMessageSender.java)：Bean，发送一个或多个消息，实际上是将消息保存到消息表，当所在事务提交后，异步发送当前事务插入的消息

[XkwRocketMQListener](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/listener/XkwRocketMQListener.java)：抽象类，接收并分发消息

[AbstractXkwIdCheckMessageHandler](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/listener/AbstractXkwIdCheckMessageHandler.java)：抽象类，检查幂等性并处理消息，消息及异常信息会保存到数据库

[AbstractXkwMessageHandler](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/listener/AbstractXkwMessageHandler.java)：抽象类，不检查幂等性，也不会保存消息或异常信息，直接处理消息

[XkwMessageHandler](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/listener/XkwMessageHandler.java)：注解，标识类是一个MessageHandler

[XkwRocketmqProducerService](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/service/XkwRocketmqProducerService.java)：Bean，封装了一些查询生产者消息的便捷方法

[XkwRocketmqConsumerService](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/service/XkwRocketmqConsumerService.java)：Bean，封装了一些查询消费者消息的便捷方法

[XkwRocketmqConsumerMessageCleanTask](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/task/XkwRocketmqConsumerMessageCleanTask.java)：Bean，每天凌晨2点清理过期的消费者消息，如果将过期天数设置为0或负数，就不会创建这个定时任务

[XkwRocketmqProducerMessageCleanTask](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/task/XkwRocketmqProducerMessageCleanTask.java)：Bean，每天凌晨2点清理过期的生产者消息，如果将过期天数设置为0或负数，就不会创建这个定时任务

[XkwRocketmqProducerMessageScanTask](./xkw-rocketmq-spring-core/src/main/java/com/xkw/bcom/rocketmq/core/task/XkwRocketmqProducerMessageScanTask.java)：Bean，本地事务提交时自动发送的消息可能会失败，这个任务会定期检查发送失败的消息，进行补偿，各节点会通过分布式锁进行协调，并会定期唤醒，防止单点故障

