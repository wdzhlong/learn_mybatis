package com.mybatis.mapper;

import com.mybatis.entity.Role;
import org.apache.ibatis.annotations.Select;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 11:06
 * @modified By:
 * @description:
 */
public interface RoleMapper2 {

    @Select(value = "select * from t_role where id = #{id}")
    Role getRole(Long id);
}
