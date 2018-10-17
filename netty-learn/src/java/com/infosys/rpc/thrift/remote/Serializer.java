package com.infosys.rpc.thrift.remote;

/**
 * 序列化接口
 * 
 */
public interface Serializer {
	
	public byte[] getBytes(Object obj) throws Exception;
	
	public <T> T getObject(byte[] bytes) throws Exception;
	
}