package com.missxhh.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/**
 * 分片任务
 * @author hjf
 * @date 2021/1/18
 **/
@Component
public class CustomShardingJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("custom sharding job begin~~~~~~!");
        System.out.println("sharding parameter: " + shardingContext.getShardingParameter());
        // [beijing,shanghai]
        System.out.println("custom sharding job end~~~~~~!");
    }
}
