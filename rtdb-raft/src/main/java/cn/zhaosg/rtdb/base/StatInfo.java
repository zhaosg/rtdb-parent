package cn.zhaosg.rtdb.base;

import java.util.concurrent.atomic.LongAdder;

public class StatInfo {
    private LongAdder counter = new LongAdder();
    private double maxQps;
    private double avgQps;
    private double latestQps;
    private long minResponseTime;
    private long maxResponseTime;
    private long testRunningTime;
    private long startRunningTime;
    private long lastTerm = -1;
    private long period = 1000000000;
    private long startCount = 0;

    public void init() {
        startRunningTime = System.nanoTime();
    }

    public synchronized void increment() {
        counter.increment();
        long nowCount = counter.longValue();
        long now = System.nanoTime();
        testRunningTime = (now - startRunningTime);
        long term = (long) Math.ceil(testRunningTime / period);
        if (term > lastTerm) {
            if (lastTerm > -1) {
                avgQps = nowCount * period * 1.0 / testRunningTime;
                latestQps = (nowCount - startCount);
            } else {
                avgQps = 0;
                latestQps = 0;
                lastTerm=1;
            }
            System.out.println(lastTerm + "---平均" + (int) (getAvgQps()) + " q/s,瞬时" + getLatestQps() + " q/s,");
            startCount = nowCount;
            lastTerm = term;
        }
    }

    public long longValue() {
        return counter.longValue();
    }

    public long getAvgQps() {
        return (int) avgQps;
    }

    public long getLatestQps() {
        return (int) avgQps;
    }
}
