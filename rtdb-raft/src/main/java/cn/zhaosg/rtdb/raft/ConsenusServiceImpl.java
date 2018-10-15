package cn.zhaosg.rtdb.raft;


import cn.zhaosg.rtdb.base.Client;
import cn.zhaosg.rtdb.base.NettyClient;

import java.util.function.Consumer;

public class ConsenusServiceImpl implements ConsenusService {

    private Client client = new Client();

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