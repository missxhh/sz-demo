package com.missxhh.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RPC服务暴露
 * @author hjf
 **/
public class ServerExpose {

    // 线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 实例容器
    private static Map<String, Class> serviceHolder = new HashMap<String, Class>();

    // 服务主机
    private static String HOST = "127.0.0.1";

    // 对外端口
    private static int PORT = 9000;

    // 注册服务
    public void registerService(String className, Class serviceImpl){
        serviceHolder.put(className, serviceImpl);
    }

    // 服务启动
    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(HOST, PORT));
            System.out.println("==============RPC服务启动成功，服务端口：" + PORT + " ==============");

            // 等待客户端连接
            while (true) {
                executorService.execute(new ServerTask(serverSocket.accept()));
            }
        } catch (IOException e) {
            System.out.println("==============服务启动失败==============");
            e.printStackTrace();
        }
    }

    // 服务线程
    private static class ServerTask implements Runnable {

        private Socket client;

        public ServerTask(Socket clientSocket){
            this.client = clientSocket;
        }

        @Override
        public void run() {
            try {
                System.out.println("RPC接受到新的连接");
                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());

                // 实例名
                String serviceName = objectInputStream.readUTF();
                // 方法名
                String methodName = objectInputStream.readUTF();
                // 方法参数类型
                Class<?> [] paramTypes = (Class<?>[]) objectInputStream.readObject();
                Object [] args = (Object[]) objectInputStream.readObject();

                Class invokeService = serviceHolder.get(serviceName);
                if(invokeService == null) {
                    throw new ClassNotFoundException("服务" + serviceName + "不存在");
                }

                // 反射调用
                Method invokeMethod = invokeService.getMethod(methodName, paramTypes);
                Object res = invokeMethod.invoke(invokeService.newInstance(), args);

                // 将调用结果返回
                objectOutputStream.writeObject(res);
                objectOutputStream.flush();
            } catch (Exception e) {
                System.out.println("RPC服务调用异常：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
