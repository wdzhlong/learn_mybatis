package com.mybatis.mapper;

import com.mybatis.entity.Role;
import com.mybatis.entity.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 11:06
 * @modified By:
 * @description:
 */
public interface StudentMapper {
    Student getStudent(Long id);
}
