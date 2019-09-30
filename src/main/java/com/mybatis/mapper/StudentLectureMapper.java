package com.mybatis.mapper;

import com.mybatis.entity.Student;

import java.util.List;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 11:06
 * @modified By:
 * @description:
 */
public interface StudentLectureMapper {
    List<Student> findStudentLectureByStuId(Long id);
}
