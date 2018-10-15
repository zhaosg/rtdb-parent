package cn.zhaosg.rtdb.raft;

public class AppendEntriesRequest {
    private long term;//领导人的任期号
    private long leaderId;//领导人的 Id，以便于跟随者重定向请求
    private long prevLogIndex;//新的日志条目紧随之前的索引值
    private long prevLogTerm;//prevLogIndex 条目的任期号
    private LogEntry entries[];//准备存储的日志条目（表示心跳时为空；一次性发送多个是为了提高效率）
    private long leaderCommit;//领导人已经提交的日志的索引值

    public AppendEntriesRequest() {
    }

    public AppendEntriesRequest(long term,
                                long leaderId,
                                long prevLogIndex,
                                long prevLogTerm,
                                LogEntry[] entries,
                                long leaderCommit) {
        this.term = term;
        this.leaderId = leaderId;
        this.prevLogIndex = prevLogIndex;
        this.prevLogTerm = prevLogTerm;
        this.entries = entries;
        this.leaderCommit = leaderCommit;
    }

    public long getTerm() {
        return term;
    }

    public long getLeaderId() {
        return leaderId;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public long getPrevLogTerm() {
        return prevLogTerm;
    }

    public LogEntry[] getEntries() {
        return entries;
    }

    public long getLeaderCommit() {
        return leaderCommit;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public void setLeaderId(long leaderId) {
        this.leaderId = leaderId;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }

    public void setPrevLogTerm(long prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public void setEntries(LogEntry[] entries) {
        this.entries = entries;
    }

    public void setLeaderCommit(long leaderCommit) {
        this.leaderCommit = leaderCommit;
    }
}
