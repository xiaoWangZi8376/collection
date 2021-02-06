package com.lin.config;

import com.lin.action.HelloWorldJob;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    /**
     * 配置任务工厂实例
     *
     * @return
     */
    @Bean
    public JobFactory jobFactory() {
        /**
         * 采用自定义任务工厂 整合spring实例来完成构建任务*/
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        return jobFactory;
    }

    /**
     * 读取quartz.properties 文件
     * 将值初始化
     * @return
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * 初始化监听器
     * @return
     */
    @Bean
    public QuartzInitializerListener executorListener(){
        return new QuartzInitializerListener();
    }


    // 1.创建Job对象
    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        //关联我们自己的Job类
        jobDetailFactory.setJobClass(HelloWorldJob.class);
        return jobDetailFactory;
    }
    // 2.创建Trigger对象
    // 简单的Trigger
    // Cron Trigger

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean() {
        CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
        cronTriggerFactory.setJobDetail(jobDetailFactoryBean().getObject());
        //这里涉及到Cron表达式 可以去看我写的Cron表达式详解博客！！！ 此处代表每1秒钟 调用一次
        cronTriggerFactory.setCronExpression("0/1 * * * * ?");
        return cronTriggerFactory;
    }

    // 3.创建Scheduler对象
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        //关联trigger
        schedulerFactory.setTriggers(cronTriggerFactoryBean().getObject());
        schedulerFactory.setJobFactory(jobFactory());
        // 将配置文件的数据加载到SchedulerFactoryBean中
        // schedulerFactory.setQuartzProperties(quartzProperties());
        return schedulerFactory;
    }


    /**
     * 这其中我们把2个类的初始化移到了IOC中，因为之前Quartz的实例化是自己去控制的，为什么要这么做后面会有讲到。
     * 一个是SchedulerFactoryBean类，这个类其实就是之前xml配置中的SchedulerFactoryBean。
     *
     * schedulerFactoryBean是Quartz入口。同时也是spring 和Scheduler 关系的桥梁。以便在Spring容器启动后，Scheduler自动开始工作，
     * 而在Spring容器关闭前，自动关闭
     */


//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() {
//        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
//        schedulerFactoryBean.setJobFactory(jobFactory);
//        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
//        schedulerFactoryBean.setOverwriteExistingJobs(true);
//        //延长启动
//        schedulerFactoryBean.setStartupDelay(1);
//        //设置加载的配置文件
//        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));
//        return schedulerFactoryBean;
//    }


}
