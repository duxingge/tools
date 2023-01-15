package com.ut.test.config;


import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @Author wangjiaxing
 * @Date 2021/12/22
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class H2FunctionPlugin implements Interceptor {

    private static final Map<String, String> functionReplace = new HashMap<>();

    static {
        functionReplace.put("ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000)", "TIMESTAMP_MILLSECONDS()");
        functionReplace.put("INSERT IGNORE", "INSERT");
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        Iterator<Map.Entry<String, String>> iterator = functionReplace.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            sql = sql.replace(next.getKey(), next.getValue());
        }
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, sql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
