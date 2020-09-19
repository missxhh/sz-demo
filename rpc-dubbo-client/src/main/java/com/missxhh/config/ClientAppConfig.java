package com.missxhh.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.missxhh.server.order.IOrderService;
import com.missxhh.server.sms.ISmsService;
import com.missxhh.server.store.IStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/***
 * dubbo注解配置
 * @author hjf
 **/
@Configuration
@EnableDubbo(scanBasePackages = "com.missxhh.server")
@ComponentScan(value = "com.missxhh.server")
public class ClientAppConfig {

    @Reference
    private IOrderService orderService;

    @Reference
    private IStoreService storeService;

    @Reference
    private ISmsService smsService;

    @Bean
    public IOrderService orderService(){
        return orderService;
    }

    @Bean
    public IStoreService storeService(){
        return storeService;
    }

    @Bean
    public ISmsService smsService(){
        return smsService;
    }

    @Bean
    public ApplicationConfig applicationConfig(){
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("rpc-dubbo-client");
        return applicationConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig(){
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(9000);
        return protocolConfig;
    }

    @Bean
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("192.168.161.147");
        registryConfig.setPort(2181);
        return registryConfig;
    }

    @Bean
    public ProviderConfig providerConfig(){
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setSerialization("hessian2");    // 默认配置，此处可以忽略
        return providerConfig;
    }
}
