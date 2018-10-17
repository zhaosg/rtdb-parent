package com.infosys.rpc.base.cluster.pool;

import java.util.List;

/**
 * 用于提供很多工厂
 * 
 * */
public interface ClientFactoryProvider<P, R> {
	
	public List<ClientFactory<P, R>> getFactories();
	
}
