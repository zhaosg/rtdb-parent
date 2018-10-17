package com.infosys.rpc.thrift;

import com.infosys.rpc.thrift.remote.base.ThriftServicePublisher;
import org.apache.thrift.protocol.TTupleProtocol.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infosys.rpc.api.RtdbService;
import com.infosys.rpc.base.cluster.LoadBalance;
import com.infosys.rpc.base.cluster.RoundrobinLoadBalance;
import com.infosys.rpc.base.cluster.client.DistributeClient;
import com.infosys.rpc.thrift.cluster.ThriftClientFactoryProvider;
import com.infosys.rpc.thrift.remote.KryoSerializer;
import com.infosys.rpc.thrift.remote.Serializer;
import com.infosys.rpc.thrift.remote.ThriftMessageConvert;
import com.infosys.rpc.thrift.remote.base.ThriftRemoteProxyFactory;

@Configuration
//@ConditionalOnClass(ThriftClientFactoryProvider.class)
@EnableConfigurationProperties(ClientProperties.class)
public class ClientAutoConfiguration {

    @Autowired
    private ClientProperties clientProperties;

//    @Bean
//    @ConditionalOnProperty(name = "thrift.client.address")
//    Factory Factory() {
//        return new Factory();
//    }

    @Bean
    ThriftClientFactoryProvider createThriftClientFactoryProvider() {
        ThriftClientFactoryProvider thriftClientFactoryProvider = new ThriftClientFactoryProvider();
        thriftClientFactoryProvider.setConnectionTimeout(clientProperties.getTimeout());
        thriftClientFactoryProvider.setHosts(clientProperties.getAddress());
        thriftClientFactoryProvider.setConnectionTimeout(10000);
        thriftClientFactoryProvider.setFramed(true);
        thriftClientFactoryProvider.setProtocolFactory(new Factory());
        thriftClientFactoryProvider.setFrom(clientProperties.getFrom());
        thriftClientFactoryProvider.setToken(clientProperties.getToken());
        return thriftClientFactoryProvider;
    }

    @Bean
    Serializer createSerializer() {
        return new KryoSerializer();
    }

    @Bean
    LoadBalance createLoadBalance() {
        return new RoundrobinLoadBalance();
    }

    @Bean
    ThriftMessageConvert createThriftMessageConvert(Serializer serializer) {
        ThriftMessageConvert thriftMessageConvert = new ThriftMessageConvert();
        thriftMessageConvert.setSerializer(serializer);
        return thriftMessageConvert;
    }

    @Bean
    DistributeClient createDistributeClient(ThriftClientFactoryProvider thriftClientFactoryProvider,LoadBalance loadBalance) {
        DistributeClient client = new DistributeClient();
        client.setFactoryProvider(thriftClientFactoryProvider);
        client.setHeartbeat(1000);///心跳频率
        client.setMaxHeartbeatThread(1);///处理心跳的最大线程数
        client.setRetry(3);///连接池耗完直接重试，重试其他池子的次数，因此、maxWait的时间可能叠加
        client.setMaxActive(300);///由于被借走时间不一样，可能导致单个池子不够用，建议这个值大一些，可以通过maxIdle来限制长连接数量
        client.setMaxIdle(200);///最大空闲，当池内连接大于maxIdle，每次returnObject都会销毁连接，maxIdle保证了长连接的数量
        client.setMinIdle(5);///最小空闲
        client.setMaxWait(1000);///等待连接池的时间
        client.setMaxKeepMillis(-1);///连接使用多久之后被销毁
        client.setMaxSendCount(-1);///连接使用多少次之后被销毁
        client.setLoadBalance(loadBalance);
        return client;
    }

    @Bean
    ThriftRemoteProxyFactory createThriftRemoteProxyFactory(DistributeClient distributeClient, ThriftMessageConvert thriftMessageConvert) {
        ThriftRemoteProxyFactory proxyFactory = new ThriftRemoteProxyFactory();
        proxyFactory.setServiceName("RtdbService");
        proxyFactory.setProxyInterface(RtdbService.class);
        proxyFactory.setClient(distributeClient);
        proxyFactory.setMessageConvert(thriftMessageConvert);
        return proxyFactory;
    }

}

