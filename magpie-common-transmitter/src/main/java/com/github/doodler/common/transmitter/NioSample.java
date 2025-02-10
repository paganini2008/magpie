package com.github.doodler.common.transmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import com.github.doodler.common.timeseries.Metric;



/**
 * 
 * @Description: NioSample
 * @Author: Fred Feng
 * @Date: 21/01/2025
 * @Version 1.0.0
 */
public class NioSample implements Metric {

    public final LongAdder totalExecutions = new LongAdder();
    public final LongAdder failedExecutions = new LongAdder();
    public final LongAdder accumulatedExecutionTime = new LongAdder();
    public long timestamp;
    private final List<Integer> rates = new ArrayList<>();

    public long getTotalExecutionCount() {
        return totalExecutions.longValue();
    }

    public long getFailedExecutionCount() {
        return failedExecutions.longValue();
    }

    public long getAverageResponseTime() {
        long total = getTotalExecutionCount();
        if (total == 0) {
            return 0L;
        }
        return Math.round((double) accumulatedExecutionTime.longValue() / total);
    }

    public int getRate() {
        if (rates.size() == 0) {
            return 0;
        }
        return (int) Math.round(rates.stream().filter(r -> r > 0).mapToInt(Integer::intValue)
                .average().getAsDouble());
    }

    @Override
    public void setRate(int rate) {
        this.rates.add(rate);
    }

    @Override
    public Map<String, Object> represent() {
        return Map.of("totalExecutionCount", getTotalExecutionCount(), "failedExecutionCount",
                getFailedExecutionCount(), "averageResponseTime", getAverageResponseTime(), "rate",
                getRate());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("╔═══════════════════════════╦════════════════════╗\n");
        str.append(String.format("║ %-25s ║ %-18s ║\n", "Metric", "Value"));
        str.append("╠═══════════════════════════╬════════════════════╣\n");
        str.append(
                String.format("║ %-25s ║ %-18d ║\n", "Total Executions", getTotalExecutionCount()));
        str.append(String.format("║ %-25s ║ %-18d ║\n", "Failed Executions",
                getFailedExecutionCount()));
        str.append(String.format("║ %-25s ║ %-18d ║\n", "Average Response Time",
                getAverageResponseTime()));
        str.append(String.format("║ %-25s ║ %-18d ║\n", "Rate", getRate()));
        str.append("╚═══════════════════════════╩════════════════════╝");
        return str.toString();
    }

}
