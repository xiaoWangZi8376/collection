package com.lin.task;

import com.lin.cants.Cants;
import com.lin.cants.Dictionary;
import com.lin.service.AsynService;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class DefaultJobs implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String methodName = (String) jobDataMap.get(Dictionary.methodName);
        String beanId = (String) jobDataMap.get(Dictionary.beanName);
        ApplicationContext appCtx = null;
        try {
            appCtx = (ApplicationContext) jobExecutionContext.getScheduler().getContext().get("quartzApplicationContext");
            if (appCtx == null){
                System.out.println("ApplicationContext 为空-----------------------------");
            }
            Object bean = appCtx.getBean(beanId);
            Method method = bean.getClass().getMethod(methodName);
            method.invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
