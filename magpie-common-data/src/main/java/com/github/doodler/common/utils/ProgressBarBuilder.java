package com.github.doodler.common.utils;

/**
 * 
 * @Description: ProgressBar
 * @Author: Fred Feng
 * @Date: 25/01/2025
 * @Version 1.0.0
 */
public interface ProgressBarBuilder {

    default String getDescription() {
        return "";
    }

    default String getAdditionalInformation(ProgressBar progressBar) {
        return "";
    }

    default void printBar(String bar) {
        System.out.println(bar);
    }

}
