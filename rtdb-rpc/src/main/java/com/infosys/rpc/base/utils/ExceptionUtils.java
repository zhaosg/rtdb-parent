package com.infosys.rpc.base.utils;

/**
 */
public class ExceptionUtils {
	
	public static RuntimeException getRuntimeException(Throwable e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

}
