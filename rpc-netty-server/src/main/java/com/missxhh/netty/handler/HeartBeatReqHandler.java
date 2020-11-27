package com.missxhh.netty.handler;

import com.missxhh.entity.RegData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 注册中心心跳处理器
 * @author hjf
 **/
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    private ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RegData message = (RegData) msg;
        if (message.getType() == 3) {
            // 第一次心跳应答时，开启心跳任务
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0,
                    5000,
                    TimeUnit.MILLISECONDS);
            ReferenceCountUtil.release(msg);
            //如果是心跳应答
        } else if (message.getType() == 4) {
            System.out.println("心跳返回响应");
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 心跳任务
     * @author hjf
     **/
    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            System.out.println("服务端发送心跳");
            RegData heatBeat = new RegData();
            heatBeat.setType(3);
            ctx.writeAndFlush(heatBeat);
        }
    }
}
