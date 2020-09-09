package com.missxhh.server;

import com.missxhh.entity.RpcData;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端服务发现类
 **/
public class NioServerDiscover {

    // 服务主机
    private static String SERVER_HOST = "127.0.0.1";
    // 服务端口
    private static int SERVER_PORT = 9000;
    // socket通道
    private SocketChannel socketChannel;
    // 选择器
    private Selector selector;

    public NioServerDiscover(){
        try {
            // 创建打开选择器
            this.selector = Selector.open();
            // 打开通道
            socketChannel = SocketChannel.open();
            // 设置为非阻塞模式
            socketChannel.configureBlocking(false);
            if(!socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT))) {
                // 连接还没有完成，监听连接事件
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
                // 此处被阻塞住，所以没有消息
                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeySet.iterator();
                SelectionKey key = null;
                while(it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        if(key.isValid()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            if(key.isConnectable()) {
                                // 连接事件
                                if(socketChannel.finishConnect()) {
                                    System.out.println("socket连接成功");
                                } else {
                                    System.out.println("socket连接失败");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(key != null) {
                            key.cancel();
                        }
                        if(key.channel() != null) {
                            key.channel().close();
                        }
                    }
                }
            }
            System.out.println("==============NIO RPC客户端启动成功 ==============");
        } catch (IOException e) {
            System.out.println("==============NIO 客户端启动失败==============");
            e.printStackTrace();
        }
    }

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
            try {
                RpcData rpcData = new RpcData();
                rpcData.setServiceName(serverClass.getName());
                rpcData.setMethodName(method.getName());
                rpcData.setParamTypes(method.getParameterTypes());
                rpcData.setArgs(args);
                // 发送数据请求
                sendObj(rpcData);
                // 注册读事件
                socketChannel.register(selector, SelectionKey.OP_READ);
                // 获取服务返回
                obj = getRemoteRes();
            } catch (IOException e) {
                System.out.println("选择器选择事件异常");
            }
            return obj;
        }
    }

    /**
     * 获取远端服务返回
     * @author hjf
     **/
    private Object getRemoteRes() throws IOException, ClassNotFoundException {
        Object obj = null;
        // 阻塞等待返回
        selector.select();
        Set<SelectionKey> set = selector.selectedKeys();
        for (SelectionKey key : set) {
            if(key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                // 分配缓冲区
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(byteBuffer);
                if(readBytes > 0){
                    byteBuffer.flip();
                    byte [] bytes = byteBuffer.array();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    obj = objectInputStream.readObject();
                    objectInputStream.close();
                }
            }
        }
        return obj;
    }

    /**
     * 传输对象信息
     * @author hjf
     **/
    private void sendObj(RpcData rpcData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(rpcData);
        objectOutputStream.close();

        byte[] bytes = outputStream.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }
}
