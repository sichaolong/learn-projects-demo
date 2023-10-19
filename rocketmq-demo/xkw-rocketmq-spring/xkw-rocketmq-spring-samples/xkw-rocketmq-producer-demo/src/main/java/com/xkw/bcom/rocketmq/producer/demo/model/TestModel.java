/*
 * xkw.com Inc. Copyright (c) 2022 All Rights Reserved.
 */

package com.xkw.bcom.rocketmq.producer.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TestModel
 *
 * @author xuelingkang
 * @version 1.0
 * @date 2022年06月28日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestModel {

    private Integer id;
    private String name;
    private String year;
}
