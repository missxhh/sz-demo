package com.missxhh.demo.esclient.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class EsConfig {
    private Logger logger = LoggerFactory.getLogger(EsConfig.class);
    @Value("${es.server.host}")
    private String host;
    @Value("${es.server.port}")
    private int port;
    @Value("${es.cluster.name}")
    private String clusterName;

    @Bean
    public TransportClient getTransportClient() {
        logger.info("加载ES集群：" + clusterName + "，主机：" + host + "，端口：" + port );
        Settings.Builder builder = Settings.builder()
                // 配置集群名称
                .put("cluster.name", clusterName)
                // 开启集群嗅探特性，允许它动态地添加新主机并删除旧主机
                .put("client.transport.sniff", true);
        Settings settings = builder.build();
        // 客户端初始化
        TransportClient transportClient = new PreBuiltTransportClient(settings);
        try {
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(host), port);
            transportClient.addTransportAddress(transportAddress);
        } catch (UnknownHostException ex) {
            logger.error("ES集群配置异常", ex);
        }
        return transportClient;
    }
}
