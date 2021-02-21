package com.lin.cants;

public enum Cants {
    beanName_1("AsynAccept", "asynAccept"),
    beanName_2("AsynSend", "asynSend"),
    methodName("2", "doJobs");


    private String key;
    private String desc;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    Cants(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
