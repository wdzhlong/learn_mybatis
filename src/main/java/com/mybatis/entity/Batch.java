package com.mybatis.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 17:10
 * @modified By:
 * @description:批量操作
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Batch {

    Long id;

    String name;

    Integer age;
}
