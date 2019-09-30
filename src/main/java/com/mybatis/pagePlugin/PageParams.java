package com.mybatis.pagePlugin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author: zhenghailong
 * @date: 2019/9/30 17:45
 * @modified By:
 * @description:
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageParams {
    /**
     * 当前页码
     */
    Integer page;
    /**
     * 每页条数
     */
    Integer pageSize;
    /**
     * 是否启用插件
     */
    Boolean useFlag;
    /**
     * 是否检测当前页码的有效性
     */
    Boolean checkFlag;
    /**
     * 当前SQL返回总数，插件回填
     */
    Integer total;
    /**
     * SQL以当前分页的总页数，插件回填
     */
    Integer totalPage;
}
