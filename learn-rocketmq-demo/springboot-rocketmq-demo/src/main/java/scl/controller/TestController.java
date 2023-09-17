package scl.controller;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import scl.mq.RocketMqConstants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @projectName: learn-projects-demo
 * @package: scl.controller
 * @className: TestController
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/17 12:38
 * @version: 1.0
 */
@RestController
public class TestController {
    @Autowired
    private RocketMQTemplate rocketmqTemplate;

    @GetMapping("/template/test")
    public SendResult testRocketMqTemplate() {
        Message<String> msg = MessageBuilder.withPayload("Hello,RocketMQ").build();
        rocketmqTemplate.send(RocketMqConstants.TOPIC_TEST, msg);
        SendResult sendResult = rocketmqTemplate.syncSend(RocketMqConstants.TOPIC_TEST_SYNC, msg);
        System.out.println(sendResult);
        return sendResult;
    }


    /**
     * 同步发送消息
     * @throws Exception
     */
    @GetMapping("/client/sync/test")
    public void testRocketMqClientSync() throws Exception {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("test-client-producer-group");
        //（1）设置NameServer地址
        producer.setNamesrvAddr("120.46.82.131:9876");
        //（2）启动producer
        producer.start();
        for (int i = 0; i < 100; i++) {
            // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message("TopicTest"
                    /* Topic */,
                    "TagA"
                    /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                    /* Message body */
            );
            //（3）利用producer进行发送，并同步等待发送结果
            SendResult sendResult = producer.send(msg);
            //（4）
            System.out.printf("%s%n", sendResult);
        }
        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }


    /**
     * 异步发送消息
     * @throws Exception
     */
    @GetMapping("/client/async/test")
    public void testRocketMqClientAsync() throws Exception {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("test-client-producer-group");
        //（1）设置NameServer地址
        producer.setNamesrvAddr("120.46.82.131:9876");
        //（2）启动producer
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);
        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            try {
                final int index = i;
                // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
                org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message("TopicTest"
                        /* Topic */,
                        "TagA"
                        /* Tag */,
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                        /* Message body */
                );
                // 异步发送消息, 发送结果通过callback返回给客户端
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%-10d OK %s %n", index,
                                sendResult.getMsgId());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onException(Throwable e) {
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                        countDownLatch.countDown();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }
        }
        //异步发送，如果要求可靠传输，必须要等回调接口返回明确结果后才能结束逻辑，否则立即关闭Producer可能导致部分消息尚未传输成功
        countDownLatch.await(5, TimeUnit.SECONDS);
        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }

    /**
     * 发送单一消息
     * @throws Exception
     */

    @GetMapping("/client/once/test")
    public void testRocketMqClientOnce() throws Exception {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("test-client-producer-group");
        //（1）设置NameServer地址
        producer.setNamesrvAddr("120.46.82.131:9876");
        //（2）启动producer
        producer.start();
        for (int i = 0; i < 100; i++) {
            // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message("TopicTest"
                    /* Topic */,
                    "TagA"
                    /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                    /* Message body */
            );
            // 由于在oneway方式发送消息时没有请求应答处理，如果出现消息发送失败，则会因为没有重试而导致数据丢失。若数据不可丢，建议选用可靠同步或可靠异步发送方式。
            producer.sendOneway(msg);
        }
        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }


    /**
     * 发送顺序消息
     * @throws Exception
     */
    @GetMapping("/client/ordered/test")
    public void testRocketMqClientOrdered() throws Exception {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("test-client-producer-group");
            //（1）设置NameServer地址
            producer.setNamesrvAddr("120.46.82.131:9876");
            producer.start();

            String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
            for (int i = 0; i < 100; i++) {
                int orderId = i % 10;
                // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
                org.apache.rocketmq.common.message.Message msg =
                        new org.apache.rocketmq.common.message.Message(
                                "TopicTest",
                                tags[i % tags.length],
                                "KEY" + i,
                                ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, org.apache.rocketmq.common.message.Message msg, Object arg) {
                        Integer id = (Integer) arg;
                        int index = id % mqs.size();
                        return mqs.get(index);
                    }
                    // 这里传递的orderId，作为分区key
                }, orderId);

                System.out.printf("%s%n", sendResult);
            }

            producer.shutdown();
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送延时消息
     * @throws Exception
     */
    @GetMapping("/client/delay/test")
    public void testRocketMqClientDelay() throws Exception {

        // Instantiate a producer to send scheduled messages
        DefaultMQProducer producer = new DefaultMQProducer("test-client-producer-group");
        //（1）设置NameServer地址
        producer.setNamesrvAddr("120.46.82.131:9876");
        producer.start();
        // Launch producer
        producer.start();
        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message("TopicTest"
                    /* Topic */,
                    "TagA"
                    /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                    /* Message body */
            );
            // This message will be delivered to consumer 10 seconds later.
            msg.setDelayTimeLevel(3);
            // Send the message
            producer.send(msg);
        }

        // Shutdown producer after use.
        producer.shutdown();
    }


    /**
     * 发送批量消息
     * @throws Exception
     */
    @GetMapping("/client/batch/test")
    public void testRocketMqClientBatch() throws Exception {

        DefaultMQProducer producer = new DefaultMQProducer("test-client-producer-group");
        producer.setNamesrvAddr("120.46.82.131:9876");

        producer.start();

        //If you just send messages of no more than 1MiB at a time, it is easy to use batch
        //Messages of the same batch should have: same topic, same waitStoreMsgOK and no schedule support
        String topic = "BatchTest";
        List< org.apache.rocketmq.common.message.Message> messages = new ArrayList<>();
        messages.add(new  org.apache.rocketmq.common.message.Message(topic, "Tag", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new  org.apache.rocketmq.common.message.Message(topic, "Tag", "OrderID002", "Hello world 1".getBytes()));
        messages.add(new  org.apache.rocketmq.common.message.Message(topic, "Tag", "OrderID003", "Hello world 2".getBytes()));

        producer.send(messages);
    }


    /**
     * 发送事务消息
     * @throws Exception
     */
    @GetMapping("/client/transaction/test")
    public void testRocketMqClientTransaction() throws Exception {

        // 线程池
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });

        // 事务管理器
        TransactionListener transactionListener = new TransactionListenerImpl();
        TransactionMQProducer producer = new TransactionMQProducer("test-client-producer-group");
        producer.setExecutorService(executorService);
        producer.setTransactionListener(transactionListener);
        producer.start();


        for (int i = 0; i < 10; i++) {
            try {

                org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message("TopicTest"
                        /* Topic */,
                        "TagA"
                        /* Tag */,
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET)
                        /* Message body */
                );
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);

                Thread.sleep(10);
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();
    }


    /**
     * 实现事务管理器
     */
    static class TransactionListenerImpl implements TransactionListener {
        private AtomicInteger transactionIndex = new AtomicInteger(0);

        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

        /**
         * 执行事务
         * @param msg
         * @param arg
         * @return
         */
        @Override
        public LocalTransactionState executeLocalTransaction(org.apache.rocketmq.common.message.Message msg, Object arg) {
            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTrans.put(msg.getTransactionId(), status);
            return LocalTransactionState.UNKNOW;
        }

        /**
         * 检查事务状态
         * @param msg
         * @return
         */
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            Integer status = localTrans.get(msg.getTransactionId());
            if (null != status) {
                switch (status) {
                    case 0:
                        return LocalTransactionState.UNKNOW;
                    case 1:
                        return LocalTransactionState.COMMIT_MESSAGE;
                    case 2:
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                    default:
                        return LocalTransactionState.COMMIT_MESSAGE;
                }
            }
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }







}

