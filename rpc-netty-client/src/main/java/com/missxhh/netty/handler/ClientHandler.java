package com.missxhh.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 客户端数据接收处理器
 * @author hjf
 **/
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse() {
        return response;
    }

    @Override
    public  void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        response = obj;
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("---- 客户端消息处理异常，原因：" + cause.getMessage());
    }
}
