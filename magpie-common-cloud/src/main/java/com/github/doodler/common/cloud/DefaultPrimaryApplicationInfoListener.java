package com.github.doodler.common.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.AffectedApplicationInfo.AffectedType;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.SimpleTimer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: DefaultPrimaryApplicationInfoListener
 * @Author: Fred Feng
 * @Date: 06/02/2025
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultPrimaryApplicationInfoListener implements PrimaryApplicationInfoListener,
        ApplicationEventPublisherAware, ManagedBeanLifeCycle {

    private final ApplicationInfoManager applicationInfoManager;
    private final ApplicationInfoHolder applicationInfoHolder;
    private final PrimaryApplicationSelector primaryApplicationSelector;

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    private PrimaryApplicationInfoKeeper primaryApplicationInfoKeeper;

    @Override
    public void choosePrimaryApplication() {
        final String applicationName = applicationInfoHolder.get().getServiceId();
        Collection<ApplicationInfo> candidates =
                applicationInfoManager.getApplicationInfos(applicationName);
        if (CollectionUtils.isEmpty(candidates)) {
            if (log.isWarnEnabled()) {
                log.warn(
                        "No primary application selected because of no available applications for name: {}",
                        applicationName);
            }
        } else {
            choosePrimaryApplication(candidates);
        }
    }

    @EventListener(ApplicationInfoRegisteredEvent.class)
    public void onApplicationInfoRegistered(ApplicationInfoRegisteredEvent event) {
        choosePrimaryApplication();
        if (primaryApplicationInfoKeeper != null) {
            primaryApplicationInfoKeeper.start();
        }
    }

    @EventListener(SiblingApplicationInfoChangeEvent.class)
    public void onSiblingApplicationInfoChange(SiblingApplicationInfoChangeEvent event) {
        if (CollectionUtils.isEmpty(event.getAffectedApplications())) {
            return;
        }
        List<ApplicationInfo> offlineApplicationInfos = event.getAffectedApplications().stream()
                .filter(a -> a.getAffectedType() == AffectedType.OFFLINE)
                .map(a -> a.getApplicationInfo()).collect(Collectors.toList());
        List<ApplicationInfo> noneApplicationInfos = event.getAffectedApplications().stream()
                .filter(a -> a.getAffectedType() == AffectedType.NONE)
                .map(a -> a.getApplicationInfo()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(offlineApplicationInfos)) {
            ApplicationInfo primary = applicationInfoHolder.getPrimary();
            if (offlineApplicationInfos.contains(primary)) {
                Collection<ApplicationInfo> candidates =
                        applicationInfoManager.getApplicationInfos(primary.getServiceId());
                candidates = new ArrayList<>(candidates);
                candidates.removeAll(offlineApplicationInfos);
                if (CollectionUtils.isEmpty(candidates)) {
                    if (log.isWarnEnabled()) {
                        log.warn(
                                "No primary application selected because of no available applications for name: {}",
                                primary.getServiceId());
                    }
                } else {
                    choosePrimaryApplication(candidates);
                }
            }
        } else if (CollectionUtils.isNotEmpty(noneApplicationInfos)) {
            ApplicationInfo primary = applicationInfoHolder.getPrimary();
            if (noneApplicationInfos.contains(primary)) {
                applicationEventPublisher
                        .publishEvent(new SecondaryApplicationInfoRefreshEvent(this, primary));
            }
        }
    }

    private synchronized void choosePrimaryApplication(Collection<ApplicationInfo> candidates) {
        ApplicationInfo primaryApplicationInfo =
                primaryApplicationSelector.selectPrimary(candidates);
        applicationInfoHolder.setPrimary(primaryApplicationInfo);
        if (applicationInfoHolder.isPrimary()) {
            if (log.isInfoEnabled()) {
                log.info("{} is primary application.", applicationInfoHolder.get());
            }
            applicationEventPublisher.publishEvent(new PrimaryApplicationInfoReadyEvent(this));
        } else {
            if (log.isInfoEnabled()) {
                log.info("{} is secondary application.", applicationInfoHolder.get());
            }
            applicationEventPublisher.publishEvent(new SecondaryApplicationInfoRefreshEvent(this,
                    applicationInfoHolder.getPrimary()));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        primaryApplicationInfoKeeper = new PrimaryApplicationInfoKeeper(15, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (primaryApplicationInfoKeeper != null) {
            primaryApplicationInfoKeeper.stop();
        }
    }

    /**
     * 
     * @Description: PrimaryApplicationInfoKeeper
     * @Author: Fred Feng
     * @Date: 06/02/2025
     * @Version 1.0.0
     */
    private class PrimaryApplicationInfoKeeper extends SimpleTimer {

        PrimaryApplicationInfoKeeper(long period, TimeUnit timeUnit) {
            super(period, timeUnit);
        }

        @Override
        public boolean change() throws Exception {
            if (applicationInfoHolder.getPrimary() == null) {
                choosePrimaryApplication();
            }
            return true;
        }

    }
}
