package com.infosys.rpc.thrift.remote;

import org.nustaq.serialization.FSTConfiguration;

/**
 * @author dongjian_9@163.com
 */
public class FstSerializer implements Serializer {
    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public byte[] getBytes(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return conf.asByteArray(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(byte[] bytes) throws Exception {
        if (bytes == null) {
            return null;
        }
        return (T)conf.asObject(bytes);
    }

}