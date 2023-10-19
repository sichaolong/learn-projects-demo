/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.controller;

import com.xkw.bcom.rocketmq.producer.demo.service.PropagationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * PropagationController
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月23日
 */
@RestController
@RequestMapping("/propagation")
public class PropagationController {

    @Resource
    private PropagationService propagationService;

    @GetMapping("/require_new")
    public String concurrent2() {
        propagationService.test1();
        return "ok";
    }
}
