package com.missxhh;


import com.missxhh.server.DubboServerExpose;
import com.missxhh.server.DubboServerExposeByAnnotation;
import com.missxhh.server.DubboServerExposeByXml;

/**
 * Dubbo服务
 * @author hjf
 **/
public class DubboServerApplication {

    public static void main(String[] args) {
        DubboServerExpose serverApp;
        // 通过注解的方式启动
        // serverApp = new DubboServerExposeByAnnotation();
        // 通过XML的方式启动
        serverApp = new DubboServerExposeByXml();
        while(true) {}
    }
}
