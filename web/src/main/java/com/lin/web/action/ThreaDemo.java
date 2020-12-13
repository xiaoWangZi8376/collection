package com.lin.web.action;

import lombok.SneakyThrows;

public class ThreaDemo implements Runnable {
    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(1);
        System.out.println("线程名称：" + Thread.currentThread().getId());
    }
}
