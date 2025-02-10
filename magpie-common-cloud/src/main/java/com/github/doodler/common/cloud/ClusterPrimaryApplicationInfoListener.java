package com.github.doodler.common.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import com.github.doodler.common.cloud.AffectedApplicationInfo.AffectedType;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.SimpleTimer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: ClusterPrimaryApplicationInfoListener
 * @Author: Fred Feng
 * @Date: 06/02/2025
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class ClusterPrimaryApplicationInfoListener implements PrimaryApplicationInfoListener,
        ApplicationEventPublisherAware, ManagedBeanLifeCycle {

    private final ApplicationInfoManager applicationInfoManager;
    private final ApplicationInfoHolder applicationInfoHolder;
    private final PrimaryApplicationSelector primaryApplicationSelector;

    @Setter
    private ApplicationEventPublisher applicationEventPublisher;

    private ClusterPrimaryApplicationInfoKeeper clusterPrimaryApplicationKeeper;

    @Override
    public void choosePrimaryApplication() {
        Map<String, Collection<ApplicationInfo>> appInfos =
                applicationInfoManager.getApplicationInfos(true);
        if (MapUtils.isNotEmpty(appInfos)) {
            Collection<ApplicationInfo> candidates =
                    appInfos.entrySet().stream().flatMap(e -> e.getValue().stream()).toList();
            chooseClusterPrimaryApplication(candidates);
        }
    }

    @EventListener(ApplicationInfoRegisteredEvent.class)
    public void onApplicationInfoRegistered(ApplicationInfoRegisteredEvent event) {
        choosePrimaryApplication();
        if (clusterPrimaryApplicationKeeper != null) {
            clusterPrimaryApplicationKeeper.start();
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
            ApplicationInfo clusterPrimary = applicationInfoHolder.getPrimary();
            if (offlineApplicationInfos.contains(clusterPrimary)) {
                Map<String, Collection<ApplicationInfo>> appInfos =
                        applicationInfoManager.getApplicationInfos(true);
                Collection<ApplicationInfo> candidates =
                        appInfos.entrySet().stream().flatMap(e -> e.getValue().stream()).toList();
                candidates = new ArrayList<>(candidates);
                candidates.removeAll(offlineApplicationInfos);
                if (CollectionUtils.isNotEmpty(candidates)) {
                    chooseClusterPrimaryApplication(candidates);
                }
            }
        } else if (CollectionUtils.isNotEmpty(noneApplicationInfos)) {
            ApplicationInfo clusterPrimary = applicationInfoHolder.getPrimary();
            if (noneApplicationInfos.contains(clusterPrimary)) {
                applicationEventPublisher.publishEvent(
                        new SecondaryApplicationInfoRefreshEvent(this, clusterPrimary));
            }
        }
    }

    private synchronized void chooseClusterPrimaryApplication(
            Collection<ApplicationInfo> candidates) {
        ApplicationInfo primaryApplicationInfo =
                primaryApplicationSelector.selectClusterPrimary(candidates);
        applicationInfoHolder.setPrimary(primaryApplicationInfo);
        if (applicationInfoHolder.isPrimary()) {
            if (log.isInfoEnabled()) {
                log.info("{} is cluster primary application.", applicationInfoHolder.get());
            }
            applicationEventPublisher.publishEvent(new PrimaryApplicationInfoReadyEvent(this));
        } else {
            if (log.isInfoEnabled()) {
                log.info("{} is cluster secondary application.", applicationInfoHolder.get());
            }
            applicationEventPublisher.publishEvent(new SecondaryApplicationInfoRefreshEvent(this,
                    applicationInfoHolder.getPrimary()));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        clusterPrimaryApplicationKeeper =
                new ClusterPrimaryApplicationInfoKeeper(15, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (clusterPrimaryApplicationKeeper != null) {
            clusterPrimaryApplicationKeeper.stop();
        }
    }

    /**
     * 
     * @Description: ClusterPrimaryApplicationInfoKeeper
     * @Author: Fred Feng
     * @Date: 06/02/2025
     * @Version 1.0.0
     */
    private class ClusterPrimaryApplicationInfoKeeper extends SimpleTimer {

        ClusterPrimaryApplicationInfoKeeper(long period, TimeUnit timeUnit) {
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
