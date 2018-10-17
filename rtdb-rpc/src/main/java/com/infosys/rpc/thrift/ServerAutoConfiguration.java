package com.infosys.rpc.thrift;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.protocol.TTupleProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.infosys.rpc.api.RtdbService;
import com.infosys.rpc.remote.ServiceDefinition;
import com.infosys.rpc.thrift.remote.KryoSerializer;
import com.infosys.rpc.thrift.remote.ThriftMessageConvert;
import com.infosys.rpc.thrift.remote.base.ThriftServicePublisher;
import com.infosys.rpc.thrift.server.ThriftHsHaServer;
import org.springframework.context.annotation.Lazy;

@Configuration
@ConditionalOnClass(ThriftServicePublisher.class)
@EnableConfigurationProperties(ServerProperties.class)
public class ServerAutoConfiguration {

    @Autowired
    @Lazy
    private ServerProperties serverProperties;

    @Bean
    @ConditionalOnProperty(name = "thrift.server.port")
    ThriftServicePublisher createThriftServicePublisher(RtdbService service) {
        ThriftServicePublisher publisher = new ThriftServicePublisher();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setServiceName(service.toString());
        serviceDefinition.setInterfaceClass(RtdbService.class);
        serviceDefinition.setImplInstance(service);
        ServiceDefinition[] serviceDefinitions = new ServiceDefinition[]{serviceDefinition};
        publisher.setDefinitions(serviceDefinitions);
        ThriftMessageConvert messageConvert;
        messageConvert = new ThriftMessageConvert();
        messageConvert.setSerializer(new KryoSerializer());
        publisher.setMessageConvert(messageConvert);
        return publisher;
    }

    @Bean
    @ConditionalOnProperty(name = "thrift.server.port")
    ThriftHsHaServer createThriftHsHaServer(ThriftServicePublisher publisher) {
        ThriftHsHaServer thriftHsHaServer = new ThriftHsHaServer();
        thriftHsHaServer.setProcessor(publisher);
        thriftHsHaServer.setMinWorkerThreads(serverProperties.getMinWorkerThreads());
        thriftHsHaServer.setWorkerThreads(serverProperties.getWorkerThreads());
        thriftHsHaServer.setPort(serverProperties.getPort());
        thriftHsHaServer.setSecurity(serverProperties.getSecurity());
        thriftHsHaServer.setStopTimeoutVal(serverProperties.getStopTimeoutVal());
        thriftHsHaServer.setClientTimeout(serverProperties.getClientTimeout());
        thriftHsHaServer.setProtocolFactory(new TTupleProtocol.Factory());
        Map<String, String> allowedFromTokens = new HashMap<>();
        String whitelist = serverProperties.getWhitelist();
        if (StringUtils.isNotBlank(whitelist)) {
            String[] ss = whitelist.split(",");
            for (int i = 0; i < ss.length; i++) {
                String[] sss = ss[i].split(":");
                allowedFromTokens.put(sss[0], sss[1]);
            }
        }
        thriftHsHaServer.setAllowedFromTokens(allowedFromTokens);
        return thriftHsHaServer;
    }

}

