package com.infosys.rpc.thrift.client;

/**
 * 不填充堆栈信息的异常
 * 
 * */
public final class SuppressedException extends Throwable {
	private static final long serialVersionUID = 1L;

	public SuppressedException(String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}