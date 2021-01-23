# SpringBoot整合Quartz定时任务



最近在做项目，项目中有个需求：需要使用定时任务，这个定时任务需要即时生效。
查看Quartz官网之后发现：Quartz提供两种基本作业存储类型：

1. RAMJobStore ：RAM也就是内存，默认情况下Quartz会将任务调度存在内存中，这种方式性能是最好的，因为内存的速度是最快的。不好的地方就是数据缺乏持久性，但程序崩溃或者重新发布的时候，所有运行信息都会丢失
2. JDBC作业存储：存到数据库之后，可以做单点也可以做集群，当任务多了之后，可以统一进行管理。关闭或者重启服务器，运行的信息都不会丢失。缺点就是运行速度快慢取决于连接数据库的快慢。

所以决定采用 JDBC作业存储的方式。

# 为什么需要持久化？

1. 以后可以做集群。
2. 任务可以进行管理，随时停止、暂停、修改任务。

# Quartz 三要素：

- Scheduler：任务调度器，所有的任务都是从这里开始。
- Trigger：触发器，定义任务执行的方式、间隔。
- JobDetail & Job ： 定义任务具体执行的逻辑。

## Scheduler

scheduler 是quartz的核心所在，所有的任务都是通过scheduler开始。 
scheduler是一个接口类，所有的具体实现类都是通过SchedulerFactory工厂类实现，但是SchedulerFactory有两个具体的实现类，如图： 
![这里写图片描述](https://img-blog.csdn.net/20180718152333841?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JpY2hlbmc0NzY5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

1. StdSchedulerFactory：默认值加载是当前工作目录下的”quartz.properties”属性文件。如果加载失败，会去加载org/quartz包下的”quartz.properties”属性文件。一般使用这个实现类就能满足我们的要求。
2. DirectSchedulerFactory：这个我也没用过QAQ，听说是为那些想绝对控制 Scheduler 实例是如何生产出的人所 
   设计的。

## Trigger

惊奇的发现trigger采用的也是buidler模式-。-（想了解什么事builder模式可以看下我的另外一篇博客https://blog.csdn.net/bicheng4769/article/details/80988996） 
这里我只给大家介绍一些常用的方法，其余的可以自己查看文档：

- **withIdentity**() 给触发器一些属性 比如名字，组名。

- **startNow**() 立刻启动

- **withSchedule**(ScheduleBuilder schedBuilder) 以某种触发器触发。

- **usingJobData**(String dataKey, Boolean value) 给具体job传递参数。

  举个创建Trigger的例子：

```
Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
                .repeatForever()).build();
```

Trigger的重点内容就是在withSchedule这个方法，从参数开始：查看`SchedulerBuilder`，这个是个`抽象类`，一共有4种具体实现方法，如图： 
![这里写图片描述](https://img-blog.csdn.net/20180718162248553?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2JpY2hlbmc0NzY5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

- **SimpleScheduleBuilder**

  最简单的触发器，表示从某一时刻开始，以一定的时间间隔执行任务。

  属性：

  - repeatInterval 重复间隔。
  - repeatCount 重复次数。

比如：现在开始，以后每一个小时执行一次。

```
Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1)
                .repeatForever()).build();
```

- **DailyTimeIntervalScheduleBuilder**

   

  每一天的某一个时间段内，以一定的时间间隔执行任务，可以指定具体的某一天（星期一、星期二、星期三。。）

  属性：

  - intervalUnit 重复间隔（秒、分钟、小时。。。）。
  - daysOfWeek 具体的星期。 默认 周一到周日
  - startTimeOfDay 每天开始时间 默认 0.0
  - endTimeOfDay 每天结束时间，默认 23.59.59
  - repeatCount 重复次数。 默认是-1 不限次数
  - interval 每次执行间隔

比如每周一到周四早上9点开始，晚上16点结束，每次执行间隔1 小时。

需要 导入静态方法：**import static org.quartz.DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule;**

```
Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
                //加入 scheduler之后立刻执行
                .startNow()
                //定时 ，每个1秒钟执行一次
                .withSchedule(dailyTimeIntervalSchedule()
                        .startingDailyAt(TimeOfDay.hourAndMinuteOfDay(9, 0)) //第天9：00开始
                        .endingDailyAt(TimeOfDay.hourAndMinuteOfDay(16, 0)) //16：00 结束
                        .onDaysOfTheWeek(MONDAY,TUESDAY,WEDNESDAY,THURSDAY) //周一至周五执行
                        .withIntervalInHours(1) //每间隔1小时执行一次
                        ).build();
```

- CalendarIntervalScheduleBuilder 

  和

  ```
  SimpleScheduleBuilder
  ```

  类似，都是表示从某一时刻开始，以一定时间间隔执行任务。但是

  ```
  SimpleScheduleBuilder
  ```

  无法指定一些特殊情况，比如每个月执行一次，每周执行一次、每一年执行一次

  属性： 

  - interval 执行间隔
  - intervalUnit 执行间隔的单位（秒，分钟，小时，天，月，年，星期）

```
  Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
                //加入 scheduler之后立刻执行
                .startNow()
                //定时 ，每个1秒钟执行一次
                .withSchedule(calendarIntervalSchedule()
                        .withIntervalInWeeks(1) //每周执行一次
                        ).build();
```

接下来是最刁的也是最常用的最自由的`CronScheduleBuilder`

- **CronScheduleBuilder**

- 以上几个例子都可以使用cron表达式来表示。

  属性： 

  - cron表达式。

```
Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
                //加入 scheduler之后立刻执行
                .startNow()
                //定时 ，每个1秒钟执行一次
                .withSchedule(cronSchedule("0 0/2 8-17 * * ?") // 每天8:00-17:00，每隔2分钟执行一次
                        ).build();123456
```

这没什么 最主要解释写cron表达式，可以自行google。

## JobDetail & Job

jobdetail 就是对job的定义，而job是具体执行的逻辑内容。 
具体的执行的逻辑需要实现 job类，并实现`execute`方法。 
**这里为什么需要有个JobDetai来作为job的定义，为什么不直接使用job？** 
**解释：如果使用jobdetail来定义，那么每次调度都会创建一个new job实例，这样带来的好处就是任务并发执行的时候，互不干扰，不会对临界资源造成影响**。

# 项目中出现的问题：

1. cron表达式怎么写？ 
   答：本来quartz框架用起来就很简单，重点都是在于cron表达式如何写。所以大家还是多google

   ## **表达式站位说明**

   *如：0 0 12 \* * ?*

   ![img](https://pic3.zhimg.com/80/v2-7e71c082b4aa6141a35f39e60b861dba_720w.jpg)corn表达式说明图

   - *星号()：可用在所有字段中，表示对应时间域的每一个时刻，例如， 在分钟字段时，表示“每分钟”；*
   - *问号（?）：该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于点位符；*
   - *减号(-)：表达一个范围，如在小时字段中使用“10-12”，则表示从10到12点，即10,11,12；*
   - *逗号(,)：表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五；*
   - *斜杠(/)：x/y表达一个等步长序列，x为起始值，y为增量步长值。如在分钟字段中使用0/15，则表示为0,15,30和45秒，而5/15在分钟字段中表示5,20,35,50，你也可以使用\*/y，它等同于0/y；*
   - *L：该字符只在日期和星期字段中使用，代表“Last”的意思，但它在两个字段中意思不同。L在日期字段中，表示这个月份的最后一天，如一月的31号，非闰年二月的28号；如果L用在星期中，则表示星期六，等同于7。但是，如果L出现在星期字段里，而且在前面有一个数值X，则表示“这个月的最后X天”，例如，6L表示该月的最后星期五；*
   - *W：该字符只能出现在日期字段里，是对前导日期的修饰，表示离该日期最近的工作日。例如15W表示离该月15号最近的工作日，如果该月15号是星期六，则匹配14号星期五；如果15日是星期日，则匹配16号星期一；如果15号是星期二，那结果就是15号星期二。但必须注意关联的匹配日期不能够跨月，如你指定1W，如果1号是星期六，结果匹配的是3号星期一，而非上个月最后的那天。W字符串只能指定单一日期，而不能指定日期范围；*
   - *LW组合：在日期字段可以组合使用LW，它的意思是当月的最后一个工作日；*
   - *井号(#)：该字符只能在星期字段中使用，表示当月某个工作日。如6#3表示当月的第三个星期五(6表示星期五，#3表示当前的第三个)，而4#5表示当月的第五个星期三，假设当月没有第五个星期三，忽略不触发；*
   - *C：该字符只在日期和星期字段中使用，代表“Calendar”的意思。它的意思是计划所关联的日期，如果日期没有被关联，则相当于日历中所有日期。例如5C在日期字段中就相当于日历5日以后的第一天。1C在星期字段中相当于星期日后的第一天。*
   - *Cron表达式对特殊字符的大小写不敏感，对代表星期的缩写英文大小写也不敏感。*

   **常用例子写法：**

   ![img](https://pic1.zhimg.com/80/v2-6091f4a10e66595b5d5a35f19ad807bc_720w.jpg)

2. 如何禁止并发执行？ 
   答：项目中出现了一种情况，本来job执行时间只需要10s，但是由于数据库量增大之后，执行时间变成了60s，而我设置的间隔时间是30s，这样就会出现上次任务还没执行完成，下次任务就开始执行了。所以，在这种情况下，我们要禁止quartz的并发操作。

   2种方式：

   - spring中将job的concurrent属性设置为false。**默认是true** 如下：

   ```
        <bean id="scheduleJob" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
           <property name="targetObject" ref="scheduleBusinessObject"/>
           <property name="targetMethod" value="doIt"/>
           <property name="concurrent" value="false"/>
       </bean>
   ```

3. job类上加上注解`@DisallowConcurrentExecution`。

```
@DisallowConcurrentExecution
public class HelloQuartz implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDetail detail = jobExecutionContext.getJobDetail();
        String name = detail.getJobDataMap().getString("name");
        System.out.println("my job name is  " + name + " at " + new Date());

    }
}
```

**注意：@DisallowConcurrentExecution是对JobDetail实例生效，如果一个job类被不同的jobdetail引用，这样是可以并发执行。**

# SpringBoot集成Quartz

我们也可以自己去将quartz和springBoot整合在一起，其实说是springBoot还不如说是sping，因为我们没有用到spirngboot的相关的快捷方式。
如果童鞋们想快速集成Quartz，立刻看到效果的话，可以直接往下翻，直接看SpirngBoot自带的Quartz插件。但我建议大家还是从spring整合Quartz开始，懂的原理，方有收获。

## Quartz初始化表

如果需要做持久化的话，数据肯定是要存在数据库的，那么到底存在哪些表呢？其实官网文档也跟我们讲过了，地址如下：
http://www.quartz-scheduler.org/documentation/quartz-2.2.x/tutorials/tutorial-lesson-09.html
其中有句话：

> JDBCJobStore works with nearly any database, it has been used widely with Oracle, PostgreSQL, MySQL, MS SQLServer, HSQLDB, and DB2. To use JDBCJobStore, you must first create a set of database tables for Quartz to use. You can find table-creation SQL scripts in the “docs/dbTables” directory of the Quartz distribution.

翻译：
大概就是支持这么多的数据库类型。如果你要使用JDBCJoBStore的话，你先要创建一些表，这些表在 “doc/dbTables”里面。“doc/dbTables” 在哪儿呢？其实都在源码里面，直接到官网下下来就行了。

## Spring整合Quartz

1. pom文件加入相关jar
2. 相关配置文件（不管是properties 还是yml。采用JDBC存储）
3. 业务逻辑层中使用。

## **pom文件**

如下所示：

```
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--quartz -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz-jobs</artifactId>
            <!-- <version>2.3.0</version> -->
        </dependency>
        <!--定时任务需要依赖context模块-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.1</version>
        </dependency>
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.2</version>
        </dependency>
        <!--&lt;!&ndash; druid数据库连接池 &ndash;&gt;-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
    </dependencies>

```

## **对应的properties 文件**

```
#使用自己的配置文件
org.quartz.jobStore.useProperties:true

#默认或是自己改名字都行
org.quartz.scheduler.instanceName: DefaultQuartzScheduler
#如果使用集群，instanceId必须唯一，设置成AUTO
org.quartz.scheduler.instanceId = AUTO


org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount: 10
org.quartz.threadPool.threadPriority: 5
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true


#存储方式使用JobStoreTX，也就是数据库
org.quartz.jobStore.class:org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass:org.quartz.impl.jdbcjobstore.StdJDBCDelegate
#是否使用集群（如果项目只部署到 一台服务器，就不用了）
org.quartz.jobStore.isClustered = false
org.quartz.jobStore.clusterCheckinInterval=20000
org.quartz.jobStore.tablePrefix = qrtz_
org.quartz.jobStore.dataSource = myDS

#配置数据源
#数据库中quartz表的表名前缀
org.quartz.dataSource.myDS.driver = com.mysql.jdbc.Driver
org.quartz.dataSource.myDS.URL = jdbc:mysql://localhost:3306/aipyun?serverTimezone=GMT&characterEncoding=utf-8
org.quartz.dataSource.myDS.user = root
org.quartz.dataSource.myDS.password = root123
org.quartz.dataSource.myDS.maxConnections = 5

```

## **核心QuartzConfiguration类：**

```
ackage com.cj.config;

import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 描述 ： quartz 配置信息
 *
 * @author caojing
 * @create 2018-12-24-16:47
 */
@Configuration
public class QuartzConfiguration {
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

```

这其中我们把2个类的初始化移到了IOC中，因为之前Quartz的实例化是自己去控制的，为什么要这么做后面会有讲到。
一个是SchedulerFactoryBean类，这个类其实就是之前xml配置中的SchedulerFactoryBean。附上之前xml配置如下（这里不需要配置，springboot建议我们少用xml配置）

```
<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    <property name="triggers">
        <list>
            <ref bean="oceanStatusCronTrigger"/>
        </list>
    </property>
</bean>

```

这个类我相信只要用过xml配置的人一定很熟悉，这是Quartz入口。同时也是spring 和Scheduler 关系的桥梁。以便在Spring容器启动后，Scheduler自动开始工作，而在Spring容器关闭前，自动关闭Scheduler。

## **JobFactory类**

```
package com.cj.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * 描述：
 *
 * @author caojing
 * @create 2018-12-26-14:03
 */
@Component
public  class JobFactory extends AdaptableJobFactory {

    @Autowired
    private AutowireCapableBeanFactory  capableBeanFactory;


    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        // 调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);
        // 进行注入
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}

```

这个类的作用就是讲Job的实例化交给IOC去进行。
其实问题在于：
**Job对象的实例化过程是在Quartz中进行的，注入的实体类是在Spring容器当中的** 所以在job中无法注入Srping容器的实体类。
解决方案：将Job Bean也纳入到Spring容器的管理之中，Spring容器自然能够为Job Bean自动装配好所需的依赖。
如何纳入：Job的创建都是通过JobFactory创建的。官网解释为证：
https://www.quartz-scheduler.org/api/2.2.1/org/quartz/spi/JobFactory.html

> A JobFactory is responsible for producing instances of Job classes.

翻译：JobFactory负责生成Job类的实例。
JobFactory 有2个实现类：AdaptableJobFactory 和 SimpleJobFactory。

1. 自定义的工厂类 JobFactory 继承 AdaptableJobFactory 。
2. 通过调用父类 AdaptableJobFactory 的方法`createJobInstance`来实现对Job的实例化。
3. 在Job实例化完以后，再调用自身方法为创建好的Job实例进行属性自动装配并将其纳入到Spring容器的管理之中。(通过AutowireCapableBeanFactory纳入)。截胡~~~~

## **UploadTask 类：**

```
package com.cj.quartzdemo;
import com.cj.controller.IndexController;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 描述：
 *
 * @author caojing
 * @create 2018-12-25-11:38
 */
@Component
@DisallowConcurrentExecution
public class UploadTask extends QuartzJobBean {
    @Autowired
    private IndexController indexController;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(new Date() + "任务开始------------------------------------");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new Date() + "任务结束------------------------------------");
    }
}


```

继承`QuartzJobBean`类，重写executeInternal方法。
附：`DisallowConcurrentExecution` 比如job执行10秒，任务是每隔5秒执行，加上这个注解，程序就会等10秒结束后再执行下一个任务。

## **indexController类：**

```
package com.cj.controller;

import com.cj.quartzdemo.UploadTask;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 描述：
 *
 * @author caojing
 * @create 2018-12-26-14:11
 */
@Controller
public class IndexController {
    @Autowired
    private Scheduler scheduler;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public void index() throws SchedulerException {
  	//cron表达式
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/8 * * * * ?");
        //根据name 和group获取当前trgger 的身份
        TriggerKey triggerKey = TriggerKey.triggerKey("cj", "123");
        CronTrigger triggerOld = null;
        try {
        	//获取 触发器的信息
            triggerOld = (CronTrigger) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        if (triggerOld == null) {
        	//将job加入到jobDetail中
            JobDetail jobDetail = JobBuilder.newJob(UploadTask.class).withIdentity("cj", "123").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("cj","123").withSchedule(cronScheduleBuilder).build();
            //执行任务
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            System.out.println("当前job已存在--------------------------------------------");
        }
    }
}


```

浏览器输入 http://localhost:8080/index 就可以看到数据库已经存储了我们写的cron表达式和相应的类。
查看数据库表（qrtz_cron_triggers）附上截图：
![任务信息](https://img-blog.csdnimg.cn/20181227094405862.png)

至此，job 已经被我们成功持久化到数据库。我们来回顾下整体的一个流程。

1. pom文件添加对应的依赖。
2. mysql数据库对应表的初始化。
3. 配置对应的properties
4. 将原来quartz控制的类的实例化交给spirng IOC控制。（对应的是核心`QuartzConfiguration`类和`JobFactory`类）
5. 业务逻辑层对job进行控制。

# 总结

其实思路整理一下，我们发现过程其实还是挺简单的，唯一可能有些困难的是对`QuartzConfiguration`类和`JobFactory`类的理解。这两个类也是整合的核心类。
但是在springboot2.0之后，我发现了一个很神奇的starter。

```
spring-boot-starter-quartz
1
```

用了这个，那两个核心的类就不需要写了，因为 `spring-boot-starter-quartz` 已经帮我们整理完成，下一章给大家介绍一种更为简单的整合方式。

