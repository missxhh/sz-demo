package com.missxhh.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * 分片任务
 * @author hjf
 * @date 2021/1/18
 **/
//@ElasticJobConf(
//        // job 名
//        name = "CustomShardingJob",
//        // 执行表达式
//        cron = "0/5 * * * * ?",
//        // 分片总数
//        shardingTotalCount = 3,
//        // 分片参数，格式: key=value,key=value
//        shardingItemParameters = "0=user0,1=user1,2=user2"
//)
public class CustomShardingJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("custom sharding job begin~~~~~~!");
        System.out.println("sharding parameter: " + shardingContext.getShardingParameter());
        System.out.println("custom sharding job end~~~~~~!");
    }
}
