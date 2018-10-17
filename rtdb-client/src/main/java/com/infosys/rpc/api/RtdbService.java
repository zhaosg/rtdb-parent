package com.infosys.rpc.api;

import java.util.Map;

public interface RtdbService {
    public Map<String, Double> value(String[] tagNames);
    public String toString();
}