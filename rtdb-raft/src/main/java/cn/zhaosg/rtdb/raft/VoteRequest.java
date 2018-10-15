package cn.zhaosg.rtdb.raft;

public class VoteRequest {
    private long term;//候选人的任期号
    private long candidateId;//请求选票的候选人的 Id
    private long lastLogIndex;//候选人的最后日志条目的索引值
    private long lastLogTerm;//候选人最后日志条目的任期号

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(long candidateId) {
        this.candidateId = candidateId;
    }

    public long getLastLogIndex() {
        return lastLogIndex;
    }

    public void setLastLogIndex(long lastLogIndex) {
        this.lastLogIndex = lastLogIndex;
    }

    public long getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(long lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }
}
