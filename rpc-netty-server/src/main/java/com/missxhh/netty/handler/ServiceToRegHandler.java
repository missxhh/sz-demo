package com.missxhh.netty.handler;

import com.missxhh.entity.RegData;
import com.missxhh.entity.RpcData;
import com.missxhh.netty.NettyInitializer;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.order.impl.OrderServiceImpl;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.sms.impl.SmsServiceImpl;
import com.missxhh.server.store.IStoreService;
import com.missxhh.server.store.impl.StoreServiceImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务服务处理器
 * @author hjf
 **/
public class ServiceToRegHandler extends ChannelInboundHandlerAdapter {

    // 服务主机
    private static String HOST = "127.0.0.1";
    // 服务端口
    private static int PORT = 9000;

    /**
     * 处理服务请求
     * @author hjf
     **/
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object  object) throws Exception {
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
        }
        channelHandlerContext.close();
    }
}
