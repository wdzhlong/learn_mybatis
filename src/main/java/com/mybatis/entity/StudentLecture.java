package com.mybatis.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 17:10
 * @modified By:
 * @description:学生成绩表
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentLecture {

    Long id;

    Long studentId;

    Long lectureId;

    Lecture lecture;

    BigDecimal grade;

    String note;
}
