package com.github.doodler.common.utils;


/**
 * 
 * @Description: ProgressBar
 * @Author: Fred Feng
 * @Date: 25/01/2025
 * @Version 1.0.0
 */
public interface ProgressBar {

    void setProgressWidth(int progressWidth);

    long getUsedTime();

    double getAverageExecutionTime();

    long getRemainingTime();

    long getTotalAmount();

    long getCurrentAmount();

}
