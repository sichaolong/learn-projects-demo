package scl.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @projectName: learn-projects-demo
 * @package: scl.consumer
 * @className: TestConsumer
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/17 12:45
 * @version: 1.0
 */

@Component
@RocketMQMessageListener(topic = "your_topic_name", consumerGroup = "your_consumer_group_name")
public class TestConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        // 处理消息的逻辑
        System.out.println("Received message: " + message);
    }
}
