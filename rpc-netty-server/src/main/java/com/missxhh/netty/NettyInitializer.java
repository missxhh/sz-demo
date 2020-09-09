package com.missxhh.netty;

import com.missxhh.netty.handler.ServiceHandler;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.order.impl.OrderServiceImpl;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.sms.impl.SmsServiceImpl;
import com.missxhh.server.store.IStoreService;
import com.missxhh.server.store.impl.StoreServiceImpl;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * channel 始化配置
 * @author hjf
 **/
public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化channel
     * @author hjf
     **/
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        // Netty 提供的日志打印处理器，用于查看网络日志
        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.ERROR));
        // Netty 报文长度处理器 - 解码，拿到实际的传输报文
        socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        // Netty 报文长度处理器 - 编码，给报文添加长度
        socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
        // 消息解码
        socketChannel.pipeline().addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        // 消息编码
        socketChannel.pipeline().addLast("encoder", new ObjectEncoder());
        // 设置连接超时时间,超时时间1分钟
        socketChannel.pipeline().addLast(new ReadTimeoutHandler(60));

        // 实例化服务处理器，并注册服务
        ServiceHandler serviceHandler = new ServiceHandler();
        serviceHandler.registerService(IOrderService.class.getName(), OrderServiceImpl.class);
        serviceHandler.registerService(IStoreService.class.getName(), StoreServiceImpl.class);
        serviceHandler.registerService(ISmsService.class.getName(), SmsServiceImpl.class);
        socketChannel.pipeline().addLast(serviceHandler);
    }

}
