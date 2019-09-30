package com.mybatis.mapper;

import com.mybatis.entity.Role;
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
public interface RoleMapper {

    Role getRole(Long id);
    List<Role> selectRoleByParam(@Param("roleName") String roleName,@Param("note") String note);
    List<Role> selectByRole(Role role);
    List<Role> selectByIds(Long[] ids);
    List<Role> selectByList(List<Integer> list);
    //多条记录封装一个map：Map<Integer,Role>:键是这条记录的主键，值是记录封装后的javaBean
    //@MapKey:告诉mybatis封装这个map的时候使用哪个属性作为map的key
    @MapKey("id")
    public Map<String, Role> getMRoleReturnMap(String roleName);
    // 返回一条记录的map；key就是列名，值就是对应的值
    public Map<String, Object> getRoleReturnMap(Integer id);
    int deleteRole(Long id);
    int insertRole(Role role);
    int insertRoleCustom(Role role);
}
