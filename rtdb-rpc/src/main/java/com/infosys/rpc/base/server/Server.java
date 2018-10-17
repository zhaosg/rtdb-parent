package com.infosys.rpc.base.server;

/**
 * 服务端的标准接口
 * 
 * */
public interface Server {
	
	public void start() throws ServerException;

	public void stop();
}
