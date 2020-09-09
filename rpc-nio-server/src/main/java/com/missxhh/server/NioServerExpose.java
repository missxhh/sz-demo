package com.missxhh.server;

import com.missxhh.entity.RpcData;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * NIO服务暴露
 * @author hjf
 **/
public class NioServerExpose {

    // 服务主机
    private static String HOST = "127.0.0.1";
    // 服务端口
    private static int PORT = 9000;
    // 选择器
    private Selector selector;
    // 实例容器
    private static Map<String, Class> serviceHolder = new HashMap<String, Class>();

    /**
     * 注册服务接口
     * @author hjf
     **/
    public void registerService(String className, Class serviceImpl){
        serviceHolder.put(className, serviceImpl);
    }

    /**
     * 服务启动
     * @author hjf
     **/
    public void start() {
        try {
            // 打开一个选择器
            selector = Selector.open();
            // 打开通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 绑定服务端口
            serverSocketChannel.socket().bind(new InetSocketAddress(HOST, PORT));
            // 注册选择器事件 - 接受连接事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("==============NIO RPC服务启动成功，服务端口：" + PORT + "==============");
        } catch (IOException e) {
            System.out.println("==============NIO RPC服务启动失败==============");
            e.printStackTrace();
        }

        // 当服务处于运行状态时将会一直执行
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeySet.iterator();
                SelectionKey key = null;
                while(it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleKey(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("select 事件处理异常");
                        if(key != null) {
                            key.cancel();
                            if(key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("选择器事件异常");
            }
        }
    }

    /**
     * 处理事件
     * @author hjf
     **/
    private void handleKey(SelectionKey key) throws IOException {
        if(key.isValid()) {

            // 服务端建立连接事件
            if(key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("RPC接受到新的连接");
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);   // 注册读事件
            }

            // 读取数据事件
            if(key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                // 创建buffer，设置缓冲区大小（1M），开始读数据
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);
                if(readBytes > 0) {
                    // 读取到数据
                    byteBuffer.flip();  // 切换读操作
                    byte [] bytes = byteBuffer.array();
                    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
                    try {
                        RpcData rpcData = (RpcData) objectInputStream.readObject();
                        objectInputStream.close();
                        Class invokeService = serviceHolder.get(rpcData.getServiceName());
                        if(invokeService == null) {
                            throw new ClassNotFoundException("服务" + rpcData.getServiceName() + "不存在");
                        }
                        // 反射调用
                        Method invokeMethod = invokeService.getMethod(rpcData.getMethodName(), rpcData.getParamTypes());
                        Object res = invokeMethod.invoke(invokeService.newInstance(), rpcData.getArgs());
                        sendObj(socketChannel, res);
                    } catch (Exception e) {
                        System.out.println("服务处理异常，信息：" + e.getMessage());
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * 传输对象信息
     * @author hjf
     **/
    private void sendObj(SocketChannel socketChannel, Object obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        byte[] bytes = outputStream.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }
}