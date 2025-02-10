package com.github.doodler.common.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @Description: IteratorProgressBar
 * @Author: Fred Feng
 * @Date: 25/01/2025
 * @Version 1.0.0
 */
public class IteratorProgressBar<T> implements ProgressBar, Iterable<T> {

    private static final String PATTERN = "%s%s | %s";
    private final Iterator<T> it;
    private final long totalAmount;
    private long currentAmount;
    private int progressWidth = 50;
    private long startTime;
    private long lastModified;
    private final List<Long> elapsedTimes = new LruList<>(256);

    public IteratorProgressBar(Collection<T> c, ProgressBarBuilder progressBarBuilder) {
        this(c.iterator(), c.size(), progressBarBuilder);
    }

    public IteratorProgressBar(Iterator<T> it, long totalAmount,
            ProgressBarBuilder progressBarBuilder) {
        this.it = it;
        this.totalAmount = totalAmount;
        this.progressBarBuilder = progressBarBuilder;
    }

    @Override
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    private final ProgressBarBuilder progressBarBuilder;

    @Override
    public long getUsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public double getAverageExecutionTime() {
        if (elapsedTimes.isEmpty()) {
            return 0;
        }
        return elapsedTimes.stream().mapToLong(Long::longValue).average().getAsDouble();
    }

    @Override
    public long getRemainingTime() {
        double avg = getAverageExecutionTime();
        return avg > 0 ? Math.round((totalAmount - currentAmount) / avg) : 0;
    }

    @Override
    public long getTotalAmount() {
        return totalAmount;
    }

    @Override
    public long getCurrentAmount() {
        return currentAmount;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return __hasNext();
            }

            @Override
            public T next() {
                return __next();
            }

        };
    }

    private boolean __hasNext() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        boolean result;
        if (result = it.hasNext()) {
            if (lastModified > 0) {
                elapsedTimes.add(System.currentTimeMillis() - lastModified);
            }
            currentAmount++;
            String bar = getBar(currentAmount, totalAmount);
            bar = String.format(PATTERN, progressBarBuilder.getDescription(), bar,
                    progressBarBuilder.getAdditionalInformation(this));
            progressBarBuilder.printBar(bar);
        }
        return result;
    }


    private T __next() {
        lastModified = System.currentTimeMillis();
        return it.next();
    }

    private String getBar(long current, long total) {
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
}
