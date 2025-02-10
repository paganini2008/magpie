package com.github.doodler.common.quartz.executor;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.events.GlobalApplicationEventListener;
import com.github.doodler.common.events.OnlineGlobalApplicationEvent;
import com.github.doodler.common.quartz.JobConstants;
import com.github.doodler.common.utils.MutableObservable;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: JobExecutionIgniter
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
@Slf4j
public class JobExecutionIgniter
        implements GlobalApplicationEventListener<OnlineGlobalApplicationEvent> {

    private final MutableObservable starterNotifier = new MutableObservable(false);
    private final MutableObservable recoveryNotifier = new MutableObservable(false);

    public void runOnApplicationReady(Runnable r) {
        starterNotifier.addObserver((ob, arg) -> {
            r.run();
        });
    }

    public void runOnApplicationRecovery(Runnable r) {
        recoveryNotifier.addObserver((ob, arg) -> {
            r.run();
        });
    }

    @EventListener({ApplicationReadyEvent.class})
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        starterNotifier.notifyObservers(null);
        if (log.isInfoEnabled()) {
            log.info("JobExecutor is ready to be called ...");
        }
    }

    @Override
    public void onGlobalApplicationEvent(OnlineGlobalApplicationEvent event) {
        if (!event.getServiceId().equals(JobConstants.DEFAULT_JOB_SERVICE_NAME)) {
            return;
        }
        recoveryNotifier.notifyObservers(null);
        if (log.isInfoEnabled()) {
            log.info("JobExecutor is recovery to be called ...");
        }
    }

}
