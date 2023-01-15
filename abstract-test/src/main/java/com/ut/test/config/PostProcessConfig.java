package com.ut.test.config;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author wangjiaxing* @Date 2021/12/2
 */
@Configuration
public class PostProcessConfig {


    @Value("${test.sql-file:db}")
    private String baseDir;

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new SqlSessionFactoryPostProcess(baseDir);
    }

    @Bean
    public Interceptor h2FunctionPlugin() {
        return new H2FunctionPlugin();
    }
}
