package com.missxhh.netty.handler;

import com.missxhh.entity.RegData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HeartHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object  object) throws Exception {
        RegData regData = (RegData) object;
        if(object == null) {
            // channelHandlerContext.fireChannelRead(object);
            channelHandlerContext.close();
            return;
        }
        if(regData.getType() == 3) {
            // 专门处理心跳
            System.out.println("reg receive heart，server ：" + regData.getServiceName() );
            RegData resData = new RegData();
            resData.setServiceName(regData.getServiceName());
            resData.setType(4);
            channelHandlerContext.writeAndFlush(resData);
        }
        channelHandlerContext.close();
    }
}
