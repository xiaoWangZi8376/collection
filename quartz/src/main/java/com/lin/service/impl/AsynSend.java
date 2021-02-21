package com.lin.service.impl;

import com.lin.service.AbstractService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AsynSend extends AbstractService {

    @Override
    public void doJobs() {
        System.out.println("执行---------AsynGo--------");
    }

}
