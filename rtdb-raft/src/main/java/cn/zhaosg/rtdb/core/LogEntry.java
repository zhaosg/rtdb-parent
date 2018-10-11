package cn.zhaosg.rtdb.core;

public class LogEntry {
    private  String category;
    private  String message;

    public LogEntry() {
    }

    public LogEntry(String category, String message) {
        this.category = category;
        this.message = message;
    }

    public String getCategory()
    {
        return category;
    }

    public String getMessage()
    {
        return message;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
