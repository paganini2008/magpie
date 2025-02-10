package com.github.doodler.common.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * 
 * @Description: DefaultProgressBarBuilder
 * @Author: Fred Feng
 * @Date: 25/01/2025
 * @Version 1.0.0
 */
public class DefaultProgressBarBuilder implements ProgressBarBuilder {

    private static final String PATTERN_ADDITIONAL = "%s/%s [%s<%s, %.2f/s]";

    @Override
    public String getDescription() {
        return "Processing: ";
    }

    @Override
    public String getAdditionalInformation(ProgressBar progressBar) {
        return String.format(PATTERN_ADDITIONAL, progressBar.getCurrentAmount(),
                progressBar.getTotalAmount(),
                DurationFormatUtils.formatDuration(progressBar.getUsedTime(), "m'm' s.SSS'S'"),
                DurationFormatUtils.formatDuration(progressBar.getRemainingTime(), "m'm' s.SSS'S'"),
                progressBar.getAverageExecutionTime());
    }

}
