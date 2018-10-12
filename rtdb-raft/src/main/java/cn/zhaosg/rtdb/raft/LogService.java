package cn.zhaosg.rtdb.raft;

import java.util.function.Consumer;

public interface LogService {
    ThreadLocal<LogService> holder = ThreadLocal.withInitial(() -> new LogServiceImpl());

    static LogService instance() {
        return holder.get();
    }

    AppendLogResponse appendLog(AppendLogRequest appendLogRequest);

    void remoteAppendLog(Member member, AppendLogRequest appendLogRequest, Consumer<AppendLogResponse> dataReady);


}