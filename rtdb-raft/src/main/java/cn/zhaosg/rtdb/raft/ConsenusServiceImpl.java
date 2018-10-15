package cn.zhaosg.rtdb.raft;


import cn.zhaosg.rtdb.base.Client;
import cn.zhaosg.rtdb.base.NettyClient;

import java.util.function.Consumer;

public class ConsenusServiceImpl implements ConsenusService {

    private Client client = new Client();

    //所有服务器上持久存在的
    private long currentTerm;//服务器最后一次知道的任期号（初始化为 0，持续递增）
    private long votedFor;//在当前获得选票的候选人的 Id
    private LogEntry log[];//日志条目集；每一个条目包含一个用户状态机执行的指令，和收到时的任期号

    //所有服务器上经常变的
    private long commitIndex;//已知的最大的已经被提交的日志条目的索引值
    private long lastApplied;//最后被应用到状态机的日志条目索引值（初始化为 0，持续递增）

    //在领导人里经常改变的 （选举后重新初始化）
    private long nextIndex[];//	对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）
    private long matchIndex[];//对于每一个服务器，已经复制给他的日志的最高索引值

    public VoteResponse hanldeVoteRequest(VoteRequest voteRequest) {
        return null;
    }

    public void sendVoteRequest(VoteRequest voteRequest, Consumer<VoteResponse> dataReady) {
        client.send(voteRequest, dataReady);
    }

    public AppendLogResponse hanldeAppendLog(AppendLogRequest appendLogRequest) {
        return new AppendLogResponse(appendLogRequest.getTerm(), 1);
    }

    public void sendAppendLogRequest(Member member, AppendLogRequest appendLogRequest, Consumer<AppendLogResponse> dataReady) {
        NettyClient connection = new NettyClient(member.getHost(), member.getPort());
        AppendLogRequest request = new AppendLogRequest(1, 1, 1, 1, null, 1);
        connection.send(request, AppendLogResponse.class, dataReady);
        connection.close();
    }
}