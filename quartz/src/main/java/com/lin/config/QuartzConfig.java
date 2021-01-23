package com.lin.config;

import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {


    /**
      1.创建Job对象
    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        //关联我们自己的Job类
        factory.setJobClass(HelloWorldJob.class);
        return factory;
    }
     // 2.创建Trigger对象
     // 简单的Trigger
     // Cron Trigger

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean) {
        CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
        factory.setJobDetail(jobDetailFactoryBean.getObject());
        //这里涉及到Cron表达式 可以去看我写的Cron表达式详解博客！！！ 此处代表每10秒钟 调用一次
        factory.setCronExpression("0/10 * * * * ?");
        return factory;
    }
     // 3.创建Scheduler对象
    public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean cronTriggerFactoryBean, QuartzAdaptableJobFactory quartzAdaptableJobFactory) {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    //关联trigger
    factory.setTriggers(cronTriggerFactoryBean.getObject());
    factory.setJobFactory(quartzAdaptableJobFactory);
    return factory;
    }
     */

    /**
     * 这其中我们把2个类的初始化移到了IOC中，因为之前Quartz的实例化是自己去控制的，为什么要这么做后面会有讲到。
     * 一个是SchedulerFactoryBean类，这个类其实就是之前xml配置中的SchedulerFactoryBean。
     *
     * schedulerFactoryBean是Quartz入口。同时也是spring 和Scheduler 关系的桥梁。以便在Spring容器启动后，Scheduler自动开始工作，
     * 而在Spring容器关闭前，自动关闭
     */

    @Autowired
    private JobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //延长启动
        schedulerFactoryBean.setStartupDelay(1);
        //设置加载的配置文件
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));
        return schedulerFactoryBean;
    }

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }

}
