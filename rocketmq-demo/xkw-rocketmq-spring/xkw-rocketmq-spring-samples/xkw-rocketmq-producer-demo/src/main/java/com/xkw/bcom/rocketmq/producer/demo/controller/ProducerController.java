/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.controller;

import com.xkw.bcom.rocketmq.core.message.XkwProducerMessage;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqProducerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ProducerConsumer
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Resource
    private XkwRocketmqProducerService producerService;

    @GetMapping("/error_messages")
    public List<XkwProducerMessage> error() {
        return producerService.listFailedMessages(0, 100);
    }
}
