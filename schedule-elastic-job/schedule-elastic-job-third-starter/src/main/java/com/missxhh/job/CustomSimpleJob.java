package com.missxhh.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * 简单任务
 * @author hjf
 * @date 2021/1/18
 **/
//@ElasticJobConf(
//        // job 名
//        name = "CustomSimpleJob",
//        // 执行表达式
//        cron = "0/5 * * * * ?"
//)
public class CustomSimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("custom simple job execute~~~~~~!");
    }
}
