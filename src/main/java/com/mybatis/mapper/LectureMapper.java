package com.mybatis.mapper;

import com.mybatis.entity.Lecture;
import com.mybatis.entity.Student;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 11:06
 * @modified By:
 * @description:
 */
public interface LectureMapper {
    Lecture getLecture(Long id);
}
