package com.infosys.rpc.thrift.server;

/**
 * 不填充堆栈信息的异常
 * 
 * */
public final class CredentialException extends Throwable {
	private static final long serialVersionUID = 1L;
	
	public CredentialException() {}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}