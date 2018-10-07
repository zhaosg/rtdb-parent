package demo;

import io.airlift.drift.annotations.ThriftMethod;
import io.airlift.drift.annotations.ThriftService;

import java.util.List;

@ThriftService
public class ScribeServiceImpl implements ScribeService
{
    @ThriftMethod
    public DriftResultCode log(List<DriftLogEntry> messages)
    {
//        messages.forEach(message -> System.out.println(message.getCategory()+"---"+message.getMessage()));
        return DriftResultCode.OK;
    }
}