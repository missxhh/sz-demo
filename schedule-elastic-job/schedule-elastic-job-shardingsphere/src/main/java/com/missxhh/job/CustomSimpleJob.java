package com.missxhh.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * 简单任务
 * @author hjf
 * @date 2021/1/18
 **/
@Component
public class CustomSimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("custom simple job execute~~~~~~!");
    }
}
