package com.infosys.rpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "thrift")
public class RpcServerProperties {
    private Integer port = 8001;
    private Integer minWorkerThreads = 5;
    private Integer workerThreads = 10;
    private Boolean security = true;
    private Integer stopTimeoutVal = 3000;
    private Integer clientTimeout = 10000;
    private String whitelist;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getMinWorkerThreads() {
        return minWorkerThreads;
    }

    public void setMinWorkerThreads(Integer minWorkerThreads) {
        this.minWorkerThreads = minWorkerThreads;
    }

    public Integer getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(Integer workerThreads) {
        this.workerThreads = workerThreads;
    }

    public Boolean getSecurity() {
        return security;
    }

    public void setSecurity(Boolean security) {
        this.security = security;
    }

    public Integer getStopTimeoutVal() {
        return stopTimeoutVal;
    }

    public void setStopTimeoutVal(Integer stopTimeoutVal) {
        this.stopTimeoutVal = stopTimeoutVal;
    }

    public Integer getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(Integer clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }
}

