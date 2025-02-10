package com.github.doodler.common.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * 
 * @Description: ProgressBar
 * @Author: Fred Feng
 * @Date: 25/01/2025
 * @Version 1.0.0
 */
public class TimeWaitProgressBar extends SimpleTimer implements ProgressBar {

    public TimeWaitProgressBar(Supplier<Long> totalAmount, Supplier<Long> currentAmount,
            Supplier<Double> averageExecutionTime, ProgressBarBuilder progressBarBuilder) {
        this(1, TimeUnit.SECONDS, totalAmount, currentAmount, averageExecutionTime,
                progressBarBuilder);
    }

    public TimeWaitProgressBar(long period, TimeUnit timeUnit, Supplier<Long> totalAmount,
            Supplier<Long> currentAmount, Supplier<Double> averageExecutionTime,
            ProgressBarBuilder progressBarBuilder) {
        super(period, timeUnit);
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.averageExecutionTime = averageExecutionTime;
        this.progressBarBuilder = progressBarBuilder;
    }

    private static final String PATTERN = "%s%s | %s";
    private final Supplier<Long> totalAmount;
    private final Supplier<Long> currentAmount;
    private final Supplier<Double> averageExecutionTime;
    private int progressWidth = 50;
    private long startTime;
    private final ProgressBarBuilder progressBarBuilder;

    @Override
    public void start() {
        super.start();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    @Override
    public long getUsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public double getAverageExecutionTime() {
        return averageExecutionTime.get();
    }

    @Override
    public long getRemainingTime() {
        double avg = getAverageExecutionTime();
        long amount = getTotalAmount() - getCurrentAmount();
        return avg > 0 && amount > 0 ? Math.round(amount / avg) : 0;
    }

    @Override
    public long getTotalAmount() {
        return totalAmount.get();
    }

    @Override
    public long getCurrentAmount() {
        return currentAmount.get();
    }

    private String getBar(long current, long total) {
        current = Math.min(current, total);
        long progress = (current * progressWidth) / total;
        StringBuilder bar = new StringBuilder();
        bar.append("[");
        for (int i = 0; i < progressWidth; i++) {
            if (i < progress) {
                bar.append("=");
            } else {
                bar.append(" ");
            }
        }
        bar.append("] ");
        long percent = (current * 100) / total;
        bar.append(String.format("%3d%%", percent));
        return bar.toString();
    }

    @Override
    public boolean change() throws Exception {
        String bar = getBar(getCurrentAmount(), getTotalAmount());
        bar = String.format(PATTERN, progressBarBuilder.getDescription(), bar,
                progressBarBuilder.getAdditionalInformation(this));
        progressBarBuilder.printBar(bar);
        return getCurrentAmount() < getTotalAmount();
    }

    @Override
    protected void handleCancellation(Exception reason) {
        String bar = getBar(getTotalAmount(), getTotalAmount());
        bar = String.format(PATTERN, progressBarBuilder.getDescription(), bar,
                progressBarBuilder.getAdditionalInformation(this));
        progressBarBuilder.printBar(bar);
    }

    public static void main(String[] args) {
        AtomicLong counter = new AtomicLong(0);
        // final long startTime = System.currentTimeMillis();
        TimeWaitProgressBar progressBar = new TimeWaitProgressBar(() -> 100L, () -> {
            return counter.incrementAndGet();
        }, () -> RandomUtils.randomDouble(1, 1000), new DefaultProgressBarBuilder());
        progressBar.start();
    }
}
