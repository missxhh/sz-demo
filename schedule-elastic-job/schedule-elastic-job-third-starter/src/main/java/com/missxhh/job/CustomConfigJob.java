package com.missxhh.job;

import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;

import java.util.Random;

/**
 * 任务配置
 * @author hjf
 * @date 2021/1/18
 **/
@ElasticJobConf(
        // job 名
        name = "CustomShardingJob",
        // 执行表达式
        cron = "0/5 * * * * ?",
        // 分片总数
        shardingTotalCount = 3,
        // 分片参数，格式: key=value,key=value
        shardingItemParameters = "0=user0,1=user1,2=user2",
        // 任务参数
        jobParameter = "custom param1",
        // 错过任务时重试
        misfire=true,
        // 任务监听器（监听开始、结束）
        listener = "com.missxhh.job.CustomConfigJob.CustomConfigJobListener",
        // 任务异常处理器
        jobExceptionHandler = "com.missxhh.handler.CustomConfigJobExceptionHandler"
)
public class CustomConfigJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("custom config job begin~~~~~~!");
        System.out.println("job parameter: " + shardingContext.getJobParameter());
        System.out.println("sharding parameter: " + shardingContext.getShardingParameter());
        System.out.println("custom config job end~~~~~~!");
        int random = new Random().nextInt(3);
        if(random == 2) {
            throw new RuntimeException("run time exception!");
        }
    }

    class CustomConfigJobListener implements ElasticJobListener {

        @Override
        public void beforeJobExecuted(ShardingContexts shardingContexts) {
            System.out.println("before custom job execute!");
        }

        @Override
        public void afterJobExecuted(ShardingContexts shardingContexts) {
            System.out.println("after custom job execute!");
        }
    }
}
