package demo;

import io.airlift.drift.annotations.ThriftMethod;
import io.airlift.drift.annotations.ThriftService;

import java.util.List;

@ThriftService
public interface ScribeService {
    @ThriftMethod
    DriftResultCode log(List<DriftLogEntry> messages);
}