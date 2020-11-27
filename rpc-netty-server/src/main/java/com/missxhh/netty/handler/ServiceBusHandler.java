package com.missxhh.netty.handler;

import com.missxhh.entity.RpcData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * 业务服务处理器
 * @author hjf
 **/
public class ServiceBusHandler extends ChannelInboundHandlerAdapter {

    /**
     * 处理服务请求
     * @author hjf
     **/
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        if(object instanceof RpcData) {
            RpcData rpcData = (RpcData) object;
            Class invokeService = ServiceHolder.getInstance().get(rpcData.getServiceName());
            if(invokeService == null) {
                throw new ClassNotFoundException("服务" + rpcData.getServiceName() + "不存在");
            }
            // 反射调用
            Method invokeMethod = invokeService.getMethod(rpcData.getMethodName(), rpcData.getParamTypes());
            Object res = invokeMethod.invoke(invokeService.newInstance(), rpcData.getArgs());
            if(res != null) {
                channelHandlerContext.writeAndFlush(res);
                channelHandlerContext.fireChannelRead(object);
            }
        } else {
            channelHandlerContext.close();
        }
    }
}
