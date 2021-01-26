package com.missxhh.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.util.ShardingUtil;
import org.springframework.stereotype.Component;

import static com.xxl.job.core.biz.model.ReturnT.SUCCESS;

/**
 * 常用自定义任务
 * @author hjf
 * @date 2021/1/19
 **/
@Component
public class CustomJob {

    /**
     * 普通任务
     * @author hjf
     * @date 2021/1/19
     * @param jobParam 任务参数
     **/
    @XxlJob("simpleJobHandler")
    public ReturnT<String> simpleJobHandler(String jobParam) {
        System.out.println("simple job handler! job param: " + jobParam);
        return SUCCESS;
    }

    /**
     * 分片任务
     * @author hjf
     * @date 2021/1/19
     * @param jobParam 任务参数
     */
    @XxlJob("shardingJobHandler")
    public ReturnT<String> shardingJobHandler(String jobParam) {
        // 当前节点Id
        int shardIndex = ShardingUtil.getShardingVo().getIndex();
        // 总共多少个分片
        int shardTotal = ShardingUtil.getShardingVo().getTotal();

        System.out.println("custom sharding job begin, total:" + shardTotal + ", currentIndex: " + shardIndex + "~~~~~~!");
        System.out.println("job param: " + jobParam);
        // todo..自定义业务分片逻辑，如：可以根据当前节点的编号进行取模

        return SUCCESS;
    }

    /**
     * 生命周期任务
     * @author hjf
     * @date 2021/1/19
     * @param jobParam 任务参数
     */
    @XxlJob(value = "lifecycleJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> lifecycleJobHandler(String jobParam) {
        System.out.println("lifecycle job execute! param: " + jobParam);
        return SUCCESS;
    }
    
    /***
     * 任务初始化时调用
     * @author hjf
     * @date 2021/1/19
     **/
    public void init(){
        System.out.println("custom job init!");
    }
    
    /***
     * 任务销毁时调用
     * @author hjf
     * @date 2021/1/19
     **/
    public void destroy(){
        System.out.println("after custom job execute!");
    }
}
