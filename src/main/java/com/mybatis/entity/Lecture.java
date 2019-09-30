package com.mybatis.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 17:10
 * @modified By:
 * @description:学生课程表
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lecture {

    Long id;

    String lectureName;

    String note;
}
