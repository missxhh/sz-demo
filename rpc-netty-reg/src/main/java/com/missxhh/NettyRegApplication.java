package com.missxhh;


import com.missxhh.netty.NettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty注册中心
 * @author hjf
 **/
public class NettyRegApplication {

    // 注册中心主机
    private static String HOST = "127.0.0.1";
    // 注册中心端口
    private static int PORT = 8900;

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();    // 负责连接
        EventLoopGroup childGroup = new NioEventLoopGroup();        // 负责网络读写

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup, childGroup)
                    .channel(NioServerSocketChannel.class)              // 服务端的Nio通道
                    .option(ChannelOption.SO_BACKLOG, 1024)     // 指定连接队列大小
                    .childHandler(new NettyInitializer());      // Netty初始化器

            ChannelFuture future = serverBootstrap.bind(HOST, PORT).sync();
            System.out.println("============= RPC Netty 注册中心启动成功，端口：" + PORT + " =============");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            childGroup.shutdownGracefully();
            eventLoopGroup.shutdownGracefully();
        }
    }
}