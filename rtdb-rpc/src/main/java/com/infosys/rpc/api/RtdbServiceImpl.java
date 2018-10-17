package com.infosys.rpc.api;

import com.infosys.rpc.ThriftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ThriftService
public class RtdbServiceImpl implements RtdbService {
    Logger logger = LoggerFactory.getLogger(RtdbServiceImpl.class);

    public Map<String, Double> value(String[] tagNames) {
        Map<String, Double> map = new HashMap<>();
        try {
            map.put("hi", 123d);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
        }
        return map;
    }
}
