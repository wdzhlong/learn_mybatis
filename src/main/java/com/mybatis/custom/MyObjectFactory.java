package com.mybatis.custom;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.Properties;

/**
 * @author: zhenghailong
 * @date: 2019/9/29 14:59
 * @modified By:
 * @description:
 */
public class MyObjectFactory extends DefaultObjectFactory {

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }
}
