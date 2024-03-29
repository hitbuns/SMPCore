package com.SMPCore.Utilities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExecutorLimitTask implements Runnable {
    ScheduledFuture<?> id;
    long countMax;
    long currentCount = 0L;
    boolean running = true;
    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);


    public ScheduledFuture<?> getId() {
        return this.id;
    }

    public ExecutorLimitTask(TimeUnit timeUnit, long initialDelay, long delayTicks, long count) {
        this.countMax = count;
        this.id = scheduledExecutorService.scheduleWithFixedDelay(this,initialDelay,delayTicks,timeUnit);
    }

    public boolean isRunning() {
        return this.running;
    }

    public long getCountMax() {
        return this.countMax;
    }

    public long getCurrentCount() {
        return this.currentCount;
    }

    public void setCountMax(long countMax) {
        this.countMax = countMax;
    }

    public void setCurrentCount(long currentCount) {
        this.currentCount = currentCount;
    }

    public void run() {
        ++this.currentCount;
        if (this.currentCount >= this.countMax) {
            this.id.cancel(true);
            this.running = false;
        }

    }
}