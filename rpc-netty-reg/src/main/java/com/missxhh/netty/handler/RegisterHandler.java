package com.missxhh.netty.handler;

import com.missxhh.entity.RegData;
import com.missxhh.entity.ServerNode;
import com.missxhh.entity.ServiceHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;

/**
 * 服务注册于发现
 * @author hjf
 **/
public class RegisterHandler extends ChannelInboundHandlerAdapter {

    /**
     * 处理服务请求
     * @author hjf
     **/
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object  object) throws Exception {
        RegData regData = (RegData) object;
        if(regData == null) {
            channelHandlerContext.close();
            return;
        }
        if(regData.getType() == 1) {
            // 服务注册
            registerService(regData);
            // 返回成功
            channelHandlerContext.writeAndFlush(true);
        } else {
            // 服务获取
            ServerNode res = getService(regData.getServiceName());
            // 将结果返回
            if(res != null) {
                channelHandlerContext.writeAndFlush(res);
            }
        }
        channelHandlerContext.close();
    }

    private synchronized void registerService(RegData regData){
        // 判断服务是否已经注册
        Set<ServerNode> servers = ServiceHolder.getInstance().get(regData.getServiceName());
        String host = regData.getHost();
        int port = regData.getPort();
        if(servers != null && servers.size() > 0) {
            boolean isRepeat = false;
            for (ServerNode node : servers) {
                if(node.getServerHost().equals(host) && node.getServerPort() == port) {
                System.out.println("服务[" + regData.getServiceName() + "]，主机[" + host + "]，端口[" + port + "]重复注册");
                isRepeat = true;
                break;
            }
        }
        if(!isRepeat) {
            ServerNode newNode = new ServerNode(host, port);
            servers.add(newNode);
            System.out.println("服务[" + regData.getServiceName() + "]，主机[" + host + "]，端口[" + port + "] 注册成功");
        }
        } else {
            servers = new HashSet<>();
            ServerNode newNode = new ServerNode(host, port);
            servers.add(newNode);
            ServiceHolder.getInstance().put(regData.getServiceName(), servers);
            System.out.println("服务[" + regData.getServiceName() + "]，主机[" + host + "]，端口[" + port + "] 注册成功");

        }
    }

    private ServerNode getService(String serverName){
        Set<ServerNode> set = ServiceHolder.getInstance().get(serverName);
        if(set == null && set.size() <= 0) {
            System.out.println("获取的服务[" + serverName + "]不存在");
            return null;
        }
        if(set.size() == 1) {
            System.out.println("分配唯一服务：[" + serverName + "]");
            return (ServerNode) set.toArray()[0];
        }
        Random r  = new Random();
        int index = r.nextInt(set.size());
        System.out.println("随机分配服务：[" + serverName + "]，随机坐标：" + index);
        return (ServerNode) set.toArray()[index];
    }

}
