package com.mybatis.mapper;

import com.mybatis.entity.Batch;
import com.mybatis.entity.Role;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 11:06
 * @modified By:
 * @description: 批量操作
 */
public interface BatchMapper {
    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<Batch> list);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 批量更新
     * @param batchList
     * @return
     */
    int batchUpdate(List<Batch> batchList);
}
