/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.consumer.demo.controller;

import com.xkw.bcom.rocketmq.core.message.XkwConsumerErrorMessageVO;
import com.xkw.bcom.rocketmq.core.service.XkwRocketmqConsumerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * ConsumerController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Resource
    private XkwRocketmqConsumerService consumerService;

    @GetMapping("/error_messages")
    public List<XkwConsumerErrorMessageVO> listErrorMessages() {
        return consumerService.listUnfinishedMessages(0, null, "int", null, 1656562015157L, 100);
    }

    @GetMapping("/count_error_messages")
    public int countErrorMessages() {
        return consumerService.countUnfinishedMessages(null, null, null, 1656562015157L);
    }

    @GetMapping("/delete")
    public void delete(long id) {
        consumerService.deleteMessages(Collections.singletonList(id));
    }
}
