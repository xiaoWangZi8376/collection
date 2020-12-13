package com.lin.web;

import com.alibaba.fastjson.JSON;
import com.lin.common.vo.ResultVO;
import com.lin.web.action.ThreadAction;
import com.lin.web.action.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})// 指定启动类
public class UserControllerTest {
    @Autowired
    private ThreadAction threadAction;
    @Autowired
    private UserController userController;

    // newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
    @Test
    public void testNewCachedThreadPool() {
        threadAction.executorService();
        System.out.println("1323");
    }

    @Test
    public void stgSysUserList() {
        ResponseEntity<ResultVO> resultVOResponseEntity = userController.sysUserList("1");
        System.out.println("resultVOResponseEntity:" + JSON.toJSONString(resultVOResponseEntity));
    }

    @Test
    public void devUserList() {
        ResponseEntity<ResultVO> resultVOResponseEntity = userController.devUserList("1");
        System.out.println("resultVOResponseEntity:" + JSON.toJSONString(resultVOResponseEntity));
    }
    @Test
    public void prdUserList() {
        ResponseEntity<ResultVO> resultVOResponseEntity = userController.prdUserList("1");
        System.out.println("resultVOResponseEntity:" + JSON.toJSONString(resultVOResponseEntity));
    }

}
