package com.lin.config;

import com.lin.action.HelloWorldJob;
import com.lin.cants.Cants;
import com.lin.cants.Dictionary;
import com.lin.task.DefaultJobs;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    /**
     * 配置任务工厂实例
     *
     * @return
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * 读取quartz.properties 文件
     * 将值初始化
     *
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
     *
     * @return
     */
    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }


    // 创建默认Job对象

    public JobDetailFactoryBean defaultJobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        Map<String, Object> jobDataAsMap = new HashMap<>();
        jobDataAsMap.put(Dictionary.methodName, Cants.methodName.getDesc());
        jobDetailFactory.setJobDataAsMap(jobDataAsMap);
        jobDetailFactory.setGroup(Dictionary.groupName);
        jobDetailFactory.setJobClass(DefaultJobs.class);
        return jobDetailFactory;
    }


    // 创建默认的trigger

    public CronTriggerFactoryBean buildTriggerFactoryBean(JobDetailFactoryBean JobDetailBean) {
        CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
        cronTriggerFactory.setJobDetail(JobDetailBean.getObject());
        //这里涉及到Cron表达式 可以去看我写的Cron表达式详解博客！！！ 此处代表每1秒钟 调用一次
        return cronTriggerFactory;
    }


    // 1.创建AsynSend对象
    @Bean
    public JobDetailFactoryBean AsynSendJobDetail() {
        JobDetailFactoryBean jobDetailFactoryBean = defaultJobDetailFactoryBean();
        jobDetailFactoryBean.getJobDataMap().put(Dictionary.beanName, Cants.beanName_2.getDesc());
        //关联我们自己的Job类
        return jobDetailFactoryBean;
    }

    // 2.创建AsynSendTrigger对象
    @Bean
    public CronTriggerFactoryBean AsynSendTrigger() {
        CronTriggerFactoryBean AsynGoTriggerFactoryBean = buildTriggerFactoryBean(AsynSendJobDetail());
        //这里涉及到Cron表达式 可以去看我写的Cron表达式详解博客！！！ 此处代表每1秒钟 调用一次
        AsynGoTriggerFactoryBean.setCronExpression("0/20 * * * * ?");
        return AsynGoTriggerFactoryBean;
    }

    // 1.创建AsynAccept对象
    @Bean
    public JobDetailFactoryBean AsynAcceptJobDetail() {
        JobDetailFactoryBean jobDetailFactoryBean = defaultJobDetailFactoryBean();
        jobDetailFactoryBean.getJobDataMap().put(Dictionary.beanName, Cants.beanName_1.getDesc());
        //关联我们自己的Job类
        return jobDetailFactoryBean;
    }

    // 2.创建AsynAcceptTrigger对象
    @Bean
    public CronTriggerFactoryBean AsynAcceptTrigger() {
        CronTriggerFactoryBean cronTriggerFactoryBean = buildTriggerFactoryBean(AsynAcceptJobDetail());
        //这里涉及到Cron表达式 可以去看我写的Cron表达式详解博客！！！ 此处代表每1秒钟 调用一次
        cronTriggerFactoryBean.setCronExpression("0/7 * * * * ?");
        return cronTriggerFactoryBean;
    }


    // 1.创建Job对象
    @Bean
    public JobDetailFactoryBean HelloWorldJobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        //关联我们自己的Job类
        jobDetailFactory.setJobClass(HelloWorldJob.class);
        return jobDetailFactory;
    }
    // 2.创建Trigger对象
    // 简单的Trigger
    // Cron Trigger

    @Bean
    public CronTriggerFactoryBean HelloWorldCronTriggerFactoryBean() {
        CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
        cronTriggerFactory.setJobDetail(HelloWorldJobDetailFactoryBean().getObject());
        //这里涉及到Cron表达式 可以去看我写的Cron表达式详解博客！！！ 此处代表每1秒钟 调用一次
        cronTriggerFactory.setCronExpression("0/5 * * * * ?");
        return cronTriggerFactory;
    }

    // 统计一的创建SchedulerFactory对象
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory,ApplicationContext context) throws IOException {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setJobFactory(jobFactory);
        // 将配置文件的数据加载到SchedulerFactoryBean中
        // schedulerFactory.setQuartzProperties(quartzProperties());
        //关联trigger
        schedulerFactory.setTriggers(
                HelloWorldCronTriggerFactoryBean().getObject(),
                AsynSendTrigger().getObject(),
                AsynAcceptTrigger().getObject());
        schedulerFactory.setApplicationContextSchedulerContextKey(Dictionary.quartzApplicationContext);
        schedulerFactory.setStartupDelay(1);
        schedulerFactory.setApplicationContext(context);
        return schedulerFactory;
    }

}
