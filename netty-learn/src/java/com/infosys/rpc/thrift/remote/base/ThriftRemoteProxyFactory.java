package com.infosys.rpc.thrift.remote.base;

import com.infosys.rpc.base.client.ThreadSafetyClient;
import com.infosys.rpc.remote.base.RemoteProxyFactory;
import com.infosys.rpc.thrift.remote.ThriftMessageConvert;
import com.infosys.rpc.thrift.support.Request;
import com.infosys.rpc.thrift.support.Response;

/**
 * Thrift实现的远程代理工厂
 * 
 * */
public class ThriftRemoteProxyFactory<T> extends RemoteProxyFactory<T, Request, Response> {
	
	public ThriftRemoteProxyFactory() {
		this.messageConvert = new ThriftMessageConvert();
	}

	public ThriftRemoteProxyFactory(Class<T> proxyInterface, ThreadSafetyClient<Request, Response> client) {
		super(proxyInterface, client, new ThriftMessageConvert());
	}

	public ThriftRemoteProxyFactory(Class<T> proxyInterface, String serviceName, ThreadSafetyClient<Request, Response> client) {
		super(proxyInterface, serviceName, client, new ThriftMessageConvert());
	}
	
}

