package com.missxhh.server;


import com.missxhh.entity.RegData;
import com.missxhh.netty.NettyInitializer;
import com.missxhh.netty.NettyToRegInitializer;
import com.missxhh.netty.handler.RegHandler;
import com.missxhh.netty.handler.ServiceHolder;
import com.missxhh.netty.handler.ServiceToRegHandler;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.order.impl.OrderServiceImpl;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.sms.impl.SmsServiceImpl;
import com.missxhh.server.store.IStoreService;
import com.missxhh.server.store.impl.StoreServiceImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * NIO服务暴露
 * @author hjf
 **/
public class NettyServerExposeToReg {

    // 服务主机
    private static String HOST = "127.0.0.1";
    // 服务端口
    private static int PORT = 9001;
    // 注册中心主机
    private static String REG_HOST = "127.0.0.1";
    // 注册中心端口
    private static int REG_PORT = 8900;

    /**
     * 服务启动
     * @author hjf
     **/
    public void start() {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();    // 负责连接
        EventLoopGroup childGroup = new NioEventLoopGroup();        // 负责网络读写

        try {
            // 实例化服务处理器，并注册服务
            NettyToRegInitializer nettyToRegInitializer = new NettyToRegInitializer();

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup, childGroup)
                    .channel(NioServerSocketChannel.class)              // 服务端的Nio通道
                    .option(ChannelOption.SO_BACKLOG, 1024)     // 指定连接队列大小
                    .childHandler(nettyToRegInitializer);      // Netty初始化器

            registerService(IOrderService.class.getName(), OrderServiceImpl.class);
            registerService(IStoreService.class.getName(), StoreServiceImpl.class);
            registerService(ISmsService.class.getName(), SmsServiceImpl.class);

            ChannelFuture future = serverBootstrap.bind(HOST, PORT).sync();
            System.out.println("============= RPC Netty 服务端启动成功，使用端口：" + PORT + " =============");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            childGroup.shutdownGracefully();
            eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 注册服务接口
     * @author hjf
     **/
    private void registerService(String className, Class serviceImpl){
        RegHandler regHandler = new RegHandler();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            RegData regData = new RegData();
            regData.setServiceName(className);
            regData.setHost(HOST);
            regData.setPort(PORT);
            regData.setType(1);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
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
                            // 注册返回
                            socketChannel.pipeline().addLast(regHandler);
                        }
                    });

            // 连接服务
            ChannelFuture channelFuture = bootstrap.connect(REG_HOST, REG_PORT).sync();
            // 发送数据请求
            channelFuture.channel().writeAndFlush(regData).sync();
            // 等待服务返回
            channelFuture.channel().closeFuture().sync();
            // 获取服务返回
            Object obj = regHandler.getResponse();
            if(obj == null) {
                System.out.println("服务[" + className + "]注册失败！");
                System.exit(1);
            }
            boolean res = (boolean) regHandler.getResponse();
            if(!res) {
                System.out.println("服务[" + className + "]注册失败！");
                System.exit(1);
            }
            System.out.println("服务[" + className + "]注册成功");
            ServiceHolder.getInstance().put(className, serviceImpl);
        } catch (Exception e) {
            System.out.println("调用远程服务失败");
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}