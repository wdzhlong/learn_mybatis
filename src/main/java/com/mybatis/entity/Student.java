package com.mybatis.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 17:10
 * @modified By:
 * @description:学生表
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {

    Long id;

    String cnname;

    Integer sex;

    String note;

    StudentCard studentCard;

    List<StudentLecture> studentLectureList;
}
