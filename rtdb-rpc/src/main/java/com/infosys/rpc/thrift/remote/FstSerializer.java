package com.infosys.rpc.thrift.remote;

import com.infosys.rpc.base.utils.CloseUtils;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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