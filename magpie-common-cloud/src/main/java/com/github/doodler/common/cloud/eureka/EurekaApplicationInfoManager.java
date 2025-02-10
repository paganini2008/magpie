package com.github.doodler.common.cloud.eureka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoManager;
import com.github.doodler.common.cloud.MetadataCollector;
import com.github.doodler.common.cloud.SiblingApplicationCondition;
import com.github.doodler.common.cloud.redis.CloudConstants;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import com.github.doodler.common.utils.JacksonUtils;
import com.github.doodler.common.utils.LangUtils;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: EurekaApplicationInfoManager
 * @Author: Fred Feng
 * @Date: 02/05/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class EurekaApplicationInfoManager implements ApplicationInfoManager, ManagedBeanLifeCycle {

    private final EurekaClient eurekaClient;
    private final com.netflix.appinfo.ApplicationInfoManager appInfoManager;
    private final ApplicationInfoHolder applicationInfoHolder;
    private final List<MetadataCollector> metadataCollectors;
    private final SiblingApplicationCondition siblingApplicationCondition;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (metadataCollectors != null) {
            Map<String, String> mergedMap = metadataCollectors.stream()
                    .map(MetadataCollector::getInitialData).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (existing, replacement) -> replacement));
            updateMetadata(mergedMap);

            applicationInfoHolder.get().setMetadata(mergedMap);
        }
    }

    @Override
    public void updateMetadata(Map<String, String> data) {
        appInfoManager.registerAppMetadata(data);
    }

    @Override
    public Map<String, String> getMetadata() {
        return appInfoManager.getInfo().getMetadata();
    }

    @Override
    public Collection<ApplicationInfo> getApplicationInfos(String applicationName) {
        List<InstanceInfo> instanceInfos =
                eurekaClient.getInstancesByVipAddress(applicationName, false);
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return Collections.emptyList();
        }
        return transferToApplicationInfos(instanceInfos);
    }

    @Override
    public Map<String, Collection<ApplicationInfo>> getApplicationInfos(boolean includedSelf) {
        Applications applications = eurekaClient.getApplications();
        Map<String, Collection<ApplicationInfo>> appInfosMap = new HashMap<>();
        applications.getRegisteredApplications().forEach(app -> {
            if (includedSelf
                    || (!includedSelf && !app.getName().equalsIgnoreCase(applicationName))) {
                Collection<ApplicationInfo> appInfos =
                        transferToApplicationInfos(app.getInstances());
                if (CollectionUtils.isNotEmpty(appInfos)) {
                    appInfosMap.put(app.getName(), appInfos);
                }
            }
        });
        return appInfosMap;
    }

    private Collection<ApplicationInfo> transferToApplicationInfos(
            Collection<InstanceInfo> instanceInfos) {
        List<InstanceInfo> copy = new ArrayList<>(instanceInfos);
        Collections.sort(copy,
                (a, b) -> LangUtils.compareTo(b.getLeaseInfo().getServiceUpTimestamp(),
                        a.getLeaseInfo().getServiceUpTimestamp()));
        List<ApplicationInfo> appInfos =
                copy.stream().map(info -> getApplicationInfo(info)).filter(info -> info != null)
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        return appInfos;
    }

    protected ApplicationInfo getApplicationInfo(InstanceInfo info) {
        String json = info.getMetadata().get(CloudConstants.METADATA_APPLICATION_INFO);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        Map<String, String> copy = new HashMap<>(info.getMetadata());
        ApplicationInfo applicationInfo = JacksonUtils.parseJson(json, ApplicationInfo.class);
        applicationInfo.setMetadata(copy);
        return applicationInfo;
    }

    @Override
    public List<ApplicationInfo> getSiblingApplicationInfos() {
        Collection<ApplicationInfo> instanceInfos = getApplicationInfos(applicationName);
        if (CollectionUtils.isEmpty(instanceInfos)) {
            return Collections.emptyList();
        }
        return instanceInfos.stream()
                .filter(info -> siblingApplicationCondition
                        .isSiblingApplication(applicationInfoHolder.get(), info))
                .collect(Collectors.toList());
    }

    @Override
    public int indexOfSiblingApplications() {
        List<ApplicationInfo> siblingApplications = getSiblingApplicationInfos();
        if (CollectionUtils.isEmpty(siblingApplications)) {
            return -1;
        }
        ApplicationInfo applicationInfo = applicationInfoHolder.get();
        return siblingApplications.indexOf(applicationInfo);
    }
}
