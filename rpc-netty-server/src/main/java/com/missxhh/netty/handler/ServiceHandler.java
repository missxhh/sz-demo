package com.missxhh.netty.handler;

import com.missxhh.entity.RpcData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务服务处理器
 * @author hjf
 **/
public class ServiceHandler extends ChannelInboundHandlerAdapter {

    // 实例容器
    private static Map<String, Class> serviceHolder = new HashMap<String, Class>();

    /**
     * 注册服务接口
     * @author hjf
     **/
    public void registerService(String className, Class serviceImpl){
        serviceHolder.put(className, serviceImpl);
    }

    /**
     * 处理服务请求
     * @author hjf
     **/
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object  object) throws Exception {
        RpcData rpcData = (RpcData) object;
        Class invokeService = serviceHolder.get(rpcData.getServiceName());
        if(invokeService == null) {
            throw new ClassNotFoundException("服务" + rpcData.getServiceName() + "不存在");
        }
        // 反射调用
        Method invokeMethod = invokeService.getMethod(rpcData.getMethodName(), rpcData.getParamTypes());
        Object res = invokeMethod.invoke(invokeService.newInstance(), rpcData.getArgs());
        if(res != null) {
            channelHandlerContext.writeAndFlush(res);
        }
        channelHandlerContext.close();
    }
}
