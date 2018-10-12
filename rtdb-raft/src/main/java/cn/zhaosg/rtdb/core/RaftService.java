package cn.zhaosg.rtdb.core;

import java.util.function.Consumer;

public interface RaftService {
    ThreadLocal<RaftService> holder = ThreadLocal.withInitial(() -> new RaftServiceImpl());

    static RaftService instance() {
        return holder.get();
    }

    AppendLogResponse appendLog(AppendLogRequest appendLogRequest);

    void remoteAppendLog(Member member, AppendLogRequest appendLogRequest, Consumer<AppendLogResponse> dataReady);


}