package com.missxhh.netty.handler;

import com.missxhh.entity.RegData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务向注册中心处理器
 * @author hjf
 **/
public class ServiceToRegHandler extends ChannelInboundHandlerAdapter {

    /**
     * 注册服务接口
     * @author hjf
     **/
    public void registerService(String className, Class serviceImpl){
        ServiceHolder.getInstance().put(className, serviceImpl);
    }

    @Override
    public  void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        RegData regData = (RegData) obj;
        if(regData.getType() == 1) {
            System.out.println("服务[" + regData.getServiceName() + "]注册成功");
            // 发起心跳
            RegData regDataSend = new RegData();
            regDataSend.setType(3);
            channelHandlerContext.fireChannelRead(regDataSend);
        } else {
            channelHandlerContext.fireChannelRead(obj);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("---- 客户端消息处理异常，原因：" + cause.getMessage());
    }
}
