package cn.zhaosg.rtdb.raft;

public interface RaftService {
    AppendLogResponse appendLog(AppendLogRequest appendLogRequest);

    AppendLogResponse remoteAppendLog(Member member, AppendLogRequest appendLogRequest);
}