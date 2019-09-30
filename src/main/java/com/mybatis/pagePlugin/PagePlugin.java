package com.mybatis.pagePlugin;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author: zhenghailong
 * @date: 2019/9/30 17:50
 * @modified By:
 * @description:
 * MyBatis插件要求提供3个注解信息：拦截对象类型(type,只能是四大对象中的一个)，方法名称(method)和方法参数(args)。
 * 由于我们拦截的是StatementHandler对象的prepare方法，它的参数是Connection对象
 */
@Intercepts(@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class}
))
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagePlugin implements Interceptor {
    /**
     * 默认页码
     */
    Integer defaultPage;
    /**
     * 默认每页条数
     */
    Integer defaultPageSize;
    /**
     * 默认是否启动插件
     */
    Boolean defaultUsePlag;
    /**
     * 默认是否检测当前页码的正确性
     */
    Boolean defaultCheckFlag;

    /**
     * 它将直接覆盖所拦截对象原有的方法，intercept里面有个参数Invocation对象，通过它可以反射调度原来对象的方法
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }

    /**
     * target是被拦截的对象，它的作用是给被拦截对象生成一个代理对象，并返回它。
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    /**
     * 允许在plugin元素中配置所需参数，方法在插件初始化的时候就被调用了一次，然后把插件对象存入到配置中，以便后面取出。
     *
     * 这里使用setProperties()方法设置配置的参数得到默认值，然后通过Plugin.wrap()方法去生成动态代理对象
     */
    @Override
    public void setProperties(Properties props) {
        String strDefaultPage = props.getProperty("default.page", "1");
        String strDefaultPageSize = props.getProperty("default.pageSize", "50");
        String strDefaultUseFlag = props.getProperty("default.useFlag", "false");
        String strDefaultCheckFlag = props.getProperty("default.checkFlag", "1");

        this.defaultPage = Integer.parseInt(strDefaultPage);
        this.defaultPageSize = Integer.parseInt(strDefaultPageSize);
        this.defaultUsePlag = Boolean.parseBoolean(strDefaultUseFlag);
        this.defaultCheckFlag = Boolean.parseBoolean(strDefaultCheckFlag);
    }

    /**
     * 从代理对象中分离出真实对象
     * @param ivt
     * @return 非代理StatementHandler对象
     */
    private StatementHandler getUnProxyObject(Invocation ivt){
        StatementHandler statementHandler = (StatementHandler)ivt.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        Object object = null;
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过循环可以分离出最原始的目标类)
        while (metaStatementHandler.hasGetter("h")){
            object = metaStatementHandler.getValue("h");
        }
        if (object == null){
            return statementHandler;
        }
        return (StatementHandler)object;
    }

    private boolean checkSelect(String sql){
        String trim = sql.trim();
        int idx = trim.toLowerCase().indexOf("select");
        return idx == 0;
    }

    private PageParams getPageParams(Object parameterObject){
        if (parameterObject == null){
            return null;
        }
        PageParams pageParams = null;
        if (parameterObject instanceof Map){
            Map<String,Object> paramMap = (Map<String,Object>)parameterObject;
            Set<String> keySet = paramMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                Object value = paramMap.get(key);
                if (value instanceof PageParams){
                    return (PageParams)value;
                }
            }
            // 继承方式
        }else if (parameterObject instanceof PageParams){
            pageParams = (PageParams) parameterObject;
        }
        return pageParams;
    }
}
