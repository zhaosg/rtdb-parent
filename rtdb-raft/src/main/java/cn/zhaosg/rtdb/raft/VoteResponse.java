package cn.zhaosg.rtdb.raft;

public class VoteResponse {
    private long term;//当前任期号，以便于候选人去更新自己的任期号
    private long voteGranted;//候选人赢得了此张选票时为真

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public long getVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(long voteGranted) {
        this.voteGranted = voteGranted;
    }
}
