package com.mybatis.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 17:10
 * @modified By:
 * @description:学生证表
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentCard {

    Long id;

    Long studentId;

    String native_;

    Date issueDate;

    Date endDate;

    String note;
}
