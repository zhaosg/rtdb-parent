package cn.zhaosg.rtdb.raft;

public class LogEntry {
    private long term;//服务器最后一次知道的任期号（初始化为 0，持续递增）
    private  String command;

    public LogEntry() {
    }

	public long getTerm() {
		return term;
	}

	public void setTerm(long term) {
		this.term = term;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
    
    
}
