package com.missxhh.server;

import com.missxhh.entity.RegData;
import com.missxhh.entity.RpcData;
import com.missxhh.entity.ServerNode;
import com.missxhh.netty.NettyInitializer;
import com.missxhh.netty.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 客户端服务发现类
 **/
public class NettyServerDiscoverFromReg {

    // 注册中心主机
    private static String REG_HOST = "127.0.0.1";
    // 注册中心端口
    private static int REG_PORT = 8900;

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

            EventLoopGroup serverEventLoopGroup = null;

            try {
                ServerNode serverNode = getRegServerNode(serverClass.getName());
                if(serverNode != null) {
                    serverEventLoopGroup = new NioEventLoopGroup();
                    ClientHandler clientHandler = new ClientHandler();
                    RpcData rpcData = new RpcData();
                    rpcData.setServiceName(serverClass.getName());
                    rpcData.setMethodName(method.getName());
                    rpcData.setParamTypes(method.getParameterTypes());
                    rpcData.setArgs(args);

                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(serverEventLoopGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .handler(new NettyInitializer(clientHandler));

                    // 连接服务
                    ChannelFuture channelFuture = bootstrap.connect(serverNode.getServerHost(), serverNode.getServerPort()).sync();
                    // 发送数据请求
                    channelFuture.channel().writeAndFlush(rpcData).sync();
                    // 等待服务返回
                    channelFuture.channel().closeFuture().sync();
                    // 获取服务返回
                    obj = clientHandler.getResponse();
                }
            } catch (Exception e) {
                System.out.println("调用远程服务失败");
            } finally {
                if(serverEventLoopGroup != null) {
                    serverEventLoopGroup.shutdownGracefully();
                }
            }
            return obj;
        }
    }

    private ServerNode getRegServerNode(String serverName){
        ServerNode serverNode = null;
        EventLoopGroup regEventLoopGroup = new NioEventLoopGroup();
        try {
            ClientHandler clientHandler = new ClientHandler();

            RegData regData = new RegData();
            regData.setServiceName(serverName);
            regData.setPort(0);
            regData.setHost("");
            regData.setType(2);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(regEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyInitializer(clientHandler));

            // 连接注册中心
            ChannelFuture channelFuture = bootstrap.connect(REG_HOST, REG_PORT).sync();
            // 发送数据请求
            channelFuture.channel().writeAndFlush(regData).sync();
            // 等待服务返回
            channelFuture.channel().closeFuture().sync();
            // 获取服务返回
            serverNode = (ServerNode) clientHandler.getResponse();
        } catch (Exception ex) {
            System.out.println("客户端提取服务节点失败~");
        } finally {
            regEventLoopGroup.shutdownGracefully();
        }
        return serverNode;
    }
}
