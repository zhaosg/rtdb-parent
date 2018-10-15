package cn.zhaosg.rtdb.raft;

import java.util.function.Consumer;

public interface ConsenusService {
    ThreadLocal<ConsenusService> holder = ThreadLocal.withInitial(() -> new ConsenusServiceImpl());

    static ConsenusService instance() {
        return holder.get();
    }

    VoteResponse hanldeVoteRequest(VoteRequest voteRequest);

    void sendVoteRequest(VoteRequest voteRequest, Consumer<VoteResponse> dataReady);

    AppendEntriesResponse hanldeAppendLog(AppendEntriesRequest appendEntriesRequest);

    void sendAppendEntriesRequest(Member member, AppendEntriesRequest appendEntriesRequest, Consumer<AppendEntriesResponse> dataReady);

}
