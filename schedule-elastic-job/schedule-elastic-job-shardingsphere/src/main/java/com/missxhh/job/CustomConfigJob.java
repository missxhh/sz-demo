package com.missxhh.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 任务配置
 * @author hjf
 * @date 2021/1/18
 **/
@Component
public class CustomConfigJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("custom config job begin~~~~~~!");
        System.out.println("job parameter: " + shardingContext.getJobParameter());
        System.out.println("sharding parameter: " + shardingContext.getShardingParameter());
        System.out.println("custom config job end~~~~~~!");
        int random = new Random().nextInt(3);
        if(random == 2) {
            throw new RuntimeException(String.format("An exception has occurred in Job, The parameter is %s", shardingContext.getShardingParameter()));
        }
    }
}
