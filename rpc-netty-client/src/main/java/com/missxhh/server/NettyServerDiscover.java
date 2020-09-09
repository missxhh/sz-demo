package com.missxhh.server;

import com.missxhh.entity.RpcData;
import com.missxhh.netty.NettyInitializer;
import com.missxhh.netty.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 客户端服务发现类
 **/
public class NettyServerDiscover {

    // 服务主机
    private static String SERVER_HOST = "127.0.0.1";
    // 服务端口
    private static int SERVER_PORT = 9000;

    /**
     * 获取服务的动态代理
     * @author hjf
     **/
    public <T> T getRemoteProxyServer(Class<?> serverClass){
        return (T) Proxy.newProxyInstance(serverClass.getClassLoader(), new Class<?>[]{serverClass}, new ServerProxy(serverClass));
    }

    // 动态代理
    class ServerProxy implements InvocationHandler {

        private Class<?> serverClass;

        public ServerProxy(Class<?> serverClass){
            this.serverClass = serverClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object obj = null;
            ClientHandler clientHandler = new ClientHandler();
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            try {
                RpcData rpcData = new RpcData();
                rpcData.setServiceName(serverClass.getName());
                rpcData.setMethodName(method.getName());
                rpcData.setParamTypes(method.getParameterTypes());
                rpcData.setArgs(args);

                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new NettyInitializer(clientHandler));

                // 连接服务
                ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, SERVER_PORT).sync();
                // 发送数据请求
                channelFuture.channel().writeAndFlush(rpcData).sync();
                // 等待服务返回
                channelFuture.channel().closeFuture().sync();
                // 获取服务返回
                obj = clientHandler.getResponse();
            } catch (Exception e) {
                System.out.println("调用远程服务失败");
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
            return obj;
        }
    }
}
