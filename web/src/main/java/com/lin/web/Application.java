package com.lin.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = {"com.lin.*"}) //指定扫描包路径
@MapperScan(basePackages = {"com.lin.common"})
//@EnableTransactionManagement //开启事务管理
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    /** 配置PageHelper分页插件后，由于多数据源，会出现 在系统中发现了多个分页插件，请检查系统配置！

     此时需要在 Springboot启动类上添加

     @SpringBootApplication(exclude = PageHelperAutoConfiguration.class)
     */
}