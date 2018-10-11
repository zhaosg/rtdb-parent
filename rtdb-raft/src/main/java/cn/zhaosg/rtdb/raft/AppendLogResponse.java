package cn.zhaosg.rtdb.raft;

public class AppendLogResponse {
    private  long term;//当前的任期号，用于领导人去更新自己
    private  long prevLogIndex;//跟随者包含了匹配上 prevLogIndex 和 prevLogTerm 的日志时为真

    public AppendLogResponse() {
    }

    public AppendLogResponse(long term,
                             long prevLogIndex) {
        this.term = term;
        this.prevLogIndex = prevLogIndex;
    }

    public long getTerm() {
        return term;
    }

    public long getPrevLogIndex() {
        return prevLogIndex;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public void setPrevLogIndex(long prevLogIndex) {
        this.prevLogIndex = prevLogIndex;
    }
}
