package com.infosys.rpc;

import com.infosys.rpc.api.RtdbService;
import com.infosys.rpc.api.RtdbServiceImpl;
import com.infosys.rpc.remote.ServiceDefinition;
import com.infosys.rpc.thrift.remote.KryoSerializer;
import com.infosys.rpc.thrift.remote.ThriftMessageConvert;
import com.infosys.rpc.thrift.remote.base.ThriftServicePublisher;
import com.infosys.rpc.thrift.server.ThriftHsHaServer;
import org.apache.thrift.protocol.TTupleProtocol;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private ThriftServicePublisher servicePublisher;
    private ThriftHsHaServer thriftHsHaServer;
    private RtdbService rtdbService;
    private ServiceDefinition[] serviceDefinitions;
    private ThriftMessageConvert messageConvert;

    private void buildServer() {
        servicePublisher = new ThriftServicePublisher();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setServiceName("RtdbService");
        serviceDefinition.setInterfaceClass(RtdbService.class);
        rtdbService = new RtdbServiceImpl();
        serviceDefinition.setImplInstance(rtdbService);
        serviceDefinitions = new ServiceDefinition[]{serviceDefinition};
        servicePublisher.setDefinitions(serviceDefinitions);
        messageConvert = new ThriftMessageConvert();
        messageConvert.setSerializer(new KryoSerializer());
        servicePublisher.setMessageConvert(messageConvert);

        thriftHsHaServer = new ThriftHsHaServer();
        thriftHsHaServer.setProcessor(servicePublisher);
        thriftHsHaServer.setMinWorkerThreads(100);
        thriftHsHaServer.setWorkerThreads(500);
        thriftHsHaServer.setPort(8081);
        thriftHsHaServer.setSecurity(true);
        thriftHsHaServer.setStopTimeoutVal(3000);
        thriftHsHaServer.setClientTimeout(10000);
        thriftHsHaServer.setProtocolFactory(new TTupleProtocol.Factory());
        Map<String, String> allowedFromTokens = new HashMap<>();
        allowedFromTokens.put("DONGJIAN", "DSIksduiKIOYUIOkYIOhIOUIOhjklYUI");
        thriftHsHaServer.setAllowedFromTokens(allowedFromTokens);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.buildServer();
    }
}
