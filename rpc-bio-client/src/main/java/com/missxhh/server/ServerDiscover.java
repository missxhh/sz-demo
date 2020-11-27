package com.missxhh.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 客户端服务发现类
 * @author hjf
 **/
public class ServerDiscover {

    // 服务主机
    private static String SERVER_HOST = "127.0.0.1";
    // 服务端口
    private static int SERVER_PORT = 9000;

    /**
     * 获取服务的动态代理
     * @author hjf
     **/
    public static <T> T getRemoteProxyServer(Class<?> serverClass){
        InetSocketAddress address = new InetSocketAddress(SERVER_HOST, SERVER_PORT);
        return (T) Proxy.newProxyInstance(serverClass.getClassLoader(), new Class<?>[]{serverClass}, new ServerProxy(serverClass, address));
    }

    // 动态代理
    static class ServerProxy implements InvocationHandler {

        private Class<?> serverClass;
        private InetSocketAddress address;

        public ServerProxy(Class<?> serverClass, InetSocketAddress address){
            this.serverClass = serverClass;
            this.address = address;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // 调用
            // 1、建立socket连接
            // 2、写入调用的信息
            // 3、获取socket的返回

            Socket socket = null;
            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;

            try {

                // 建立socket连接
                socket = new Socket();
                socket.connect(address);

                // 写入socket信息
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeUTF(serverClass.getName());
                objectOutputStream.writeUTF(method.getName());
                objectOutputStream.writeObject(method.getParameterTypes());
                objectOutputStream.writeObject(args);
                objectOutputStream.flush();

                // 获取socket返回
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                return objectInputStream.readObject();
            } catch (Exception e) {
                System.out.println("RPC远程调用失败，原因：" + e.getMessage());
            } finally {
                if(socket != null) {
                    socket.close();
                }
                if(objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if(objectInputStream != null) {
                    objectInputStream.close();
                }
            }
            return null;
        }
    }
}
