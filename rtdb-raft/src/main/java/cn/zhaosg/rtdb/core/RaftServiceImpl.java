package cn.zhaosg.rtdb.core;


import cn.zhaosg.rtdb.base.Client;

import java.util.function.Consumer;

public class RaftServiceImpl implements RaftService {
    private Client client = new Client();

    public AppendLogResponse appendLog(AppendLogRequest appendLogRequest) {
        return new AppendLogResponse(1, 1);
    }

    public void remoteAppendLog(Member member, AppendLogRequest appendLogRequest, Consumer<Object> dataReady) {
        client.init(member.getHost(), member.getPort());
        client.send(appendLogRequest,dataReady);
    }
}