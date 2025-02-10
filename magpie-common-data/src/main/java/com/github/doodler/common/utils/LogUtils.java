package com.github.doodler.common.utils;

import static com.github.doodler.common.Constants.NEWLINE;
import java.util.Collection;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.Marker;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: LogUtils
 * @Author: Fred Feng
 * @Date: 14/01/2025
 * @Version 1.0.0
 */
@UtilityClass
public class LogUtils {

    public void trace(String prefix, String content, Logger log, Marker marker, Object... args) {
        if (log.isTraceEnabled()) {
            if (marker != null) {
                log.trace(marker, prefix + content, args);
            } else {
                log.trace(prefix + content, args);
            }
        }
    }

    public void trace(String prefix, Collection<String> contents, Logger log, Marker marker,
            Object... args) {
        if (log.isTraceEnabled()) {
            String content =
                    contents.stream().map(l -> prefix + l).collect(Collectors.joining(NEWLINE));
            if (marker != null) {
                log.trace(marker, NEWLINE.concat(content), args);
            } else {
                log.trace(NEWLINE.concat(content), args);
            }
        }
    }

    public void debug(String prefix, String content, Logger log, Marker marker, Object... args) {
        if (log.isDebugEnabled()) {
            if (marker != null) {
                log.debug(marker, prefix + content, args);
            } else {
                log.debug(prefix + content, args);
            }
        }
    }

    public void debug(String prefix, Collection<String> contents, Logger log, Marker marker,
            Object... args) {
        if (log.isDebugEnabled()) {
            String content =
                    contents.stream().map(l -> prefix + l).collect(Collectors.joining(NEWLINE));
            if (marker != null) {
                log.debug(marker, NEWLINE.concat(content), args);
            } else {
                log.debug(NEWLINE.concat(content), args);
            }
        }
    }

    public void info(String prefix, String content, Logger log, Marker marker, Object... args) {
        if (log.isInfoEnabled()) {
            if (marker != null) {
                log.info(marker, prefix + content, args);
            } else {
                log.info(prefix + content, args);
            }
        }
    }

    public void info(String prefix, Collection<String> contents, Logger log, Marker marker,
            Object... args) {
        if (log.isInfoEnabled()) {
            String content =
                    contents.stream().map(l -> prefix + l).collect(Collectors.joining(NEWLINE));
            if (marker != null) {
                log.info(marker, NEWLINE.concat(content), args);
            } else {
                log.info(NEWLINE.concat(content), args);
            }
        }
    }

    public void warn(String prefix, String content, Logger log, Marker marker, Object... args) {
        if (log.isWarnEnabled()) {
            if (marker != null) {
                log.warn(marker, prefix + content, args);
            } else {
                log.warn(prefix + content, args);
            }
        }
    }

    public void warn(String prefix, Collection<String> contents, Logger log, Marker marker,
            Object... args) {
        log.warn(NEWLINE);
        if (log.isWarnEnabled()) {
            String content =
                    contents.stream().map(l -> prefix + l).collect(Collectors.joining(NEWLINE));
            if (marker != null) {
                log.warn(marker, NEWLINE.concat(content), args);
            } else {
                log.warn(NEWLINE.concat(content), args);
            }
        }
    }

    public void error(String prefix, String content, Logger log, Marker marker, Object... args) {
        if (log.isErrorEnabled()) {
            if (marker != null) {
                log.error(marker, prefix + content, args);
            } else {
                log.error(prefix + content, args);
            }
        }
    }

    public void error(String prefix, Collection<String> contents, Logger log, Marker marker,
            Object... args) {
        log.error(NEWLINE);
        if (log.isErrorEnabled()) {
            String content =
                    contents.stream().map(l -> prefix + l).collect(Collectors.joining(NEWLINE));
            if (marker != null) {
                log.error(marker, NEWLINE.concat(content), args);
            } else {
                log.error(NEWLINE.concat(content), args);
            }
        }
    }

}
