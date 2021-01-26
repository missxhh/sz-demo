package com.missxhh.handler;


import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandler;

import java.util.Properties;

public class CustomConfigJobExceptionHandler implements JobErrorHandler {

    @Override
    public void handleException(String jobName, Throwable throwable) {
        System.out.println("handler custom config job exception!");
    }

    @Override
    public void init(Properties properties) {
        System.out.println("handler custom config job exception handler init!");
    }

    @Override
    public String getType() {
        return "customConfigJobExceptionHandler";
    }
}