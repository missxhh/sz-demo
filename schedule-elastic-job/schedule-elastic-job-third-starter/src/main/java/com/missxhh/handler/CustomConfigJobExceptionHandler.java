package com.missxhh.handler;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;

public class CustomConfigJobExceptionHandler implements JobExceptionHandler {

    @Override
    public void handleException(String jobName, Throwable throwable) {
        System.out.println("handler custom config job exception!");
    }
}