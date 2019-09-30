package com.mybatis.mapper;

import com.mybatis.entity.StudentCard;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 11:06
 * @modified By:
 * @description:
 */
public interface StudentCardMapper {
    StudentCard findStudentCardByStudentId(Long id);
}
