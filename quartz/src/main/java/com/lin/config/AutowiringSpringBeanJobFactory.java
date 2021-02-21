package com.lin.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * 这个类的作用就是讲Job的实例化交给IOC去进行。
 * 其实问题在于：
 * Job对象的实例化过程是在Quartz中进行的，注入的实体类是在Spring容器当中的 所以在job中无法注入Srping容器的实体类。
 * 解决方案：将Job Bean也纳入到Spring容器的管理之中，Spring容器自然能够为Job Bean自动装配好所需的依赖。
 * 如何纳入：Job的创建都是通过JobFactory创建的。官网解释为证：
 */
public class AutowiringSpringBeanJobFactory extends AdaptableJobFactory implements ApplicationContextAware {

        //AutowireCapableBeanFactory 可以将一个对象添加到SpringIOC容器中，并且完成该对象注入
        private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     *  将job实例交给spring ioc托管
     * 我们在job实例实现类内可以直接使用spring注入的调用被spring ioc管理的实例
     * @param bundle
     * @return
     * @throws Exception
     */
        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object obj = super.createJobInstance(bundle);
            //将obj对象添加Spring IOC容器中，并完成注入
            this.autowireCapableBeanFactory.autowireBean(obj);
            return obj;
        }

    }

