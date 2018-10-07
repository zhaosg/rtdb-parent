package com.infosys.rpc.thrift.remote.base;

import com.infosys.rpc.remote.ServiceDefinition;
import com.infosys.rpc.remote.base.ServicePublisher;
import com.infosys.rpc.thrift.remote.ThriftMessageConvert;
import com.infosys.rpc.thrift.support.Request;
import com.infosys.rpc.thrift.support.Response;

/**
 * Thrift实现的服务发布处理器
 * 
 * @author dongjian_9@163.com
 * */
public class ThriftServicePublisher extends ServicePublisher<Request, Response> {
	
	public ThriftServicePublisher() {
		this.messageConvert = new ThriftMessageConvert();
	}

	public ThriftServicePublisher(ServiceDefinition... definitions) {
		super(new ThriftMessageConvert(), definitions);
	}
	
}

