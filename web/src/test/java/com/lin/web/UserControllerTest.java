package com.lin.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})// 指定启动类
public class UserControllerTest {
   @Test
    public void test (){
       System.out.println("1323");
   }

}
