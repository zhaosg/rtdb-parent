package cn.zhaosg.rtdb.raft;

public class LogEntry {
    private long term;//服务器最后一次知道的任期号（初始化为 0，持续递增）
    private  String command;

    public LogEntry() {
    }
}
