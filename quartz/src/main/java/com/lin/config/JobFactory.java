package com.lin.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * 这个类的作用就是讲Job的实例化交给IOC去进行。
 * 其实问题在于：
 * Job对象的实例化过程是在Quartz中进行的，注入的实体类是在Spring容器当中的 所以在job中无法注入Srping容器的实体类。
 * 解决方案：将Job Bean也纳入到Spring容器的管理之中，Spring容器自然能够为Job Bean自动装配好所需的依赖。
 * 如何纳入：Job的创建都是通过JobFactory创建的。官网解释为证：
 */
@Component
public class JobFactory extends AdaptableJobFactory {

        //AutowireCapableBeanFactory 可以将一个对象添加到SpringIOC容器中，并且完成该对象注入
        @Autowired
        private AutowireCapableBeanFactory autowireCapableBeanFactory;

        /**
         * 该方法需要将实例化的任务对象手动的添加到springIOC容器中并且完成对象的注入
         */
        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object obj = super.createJobInstance(bundle);
            //将obj对象添加Spring IOC容器中，并完成注入
            this.autowireCapableBeanFactory.autowireBean(obj);
            return obj;
        }

    }

