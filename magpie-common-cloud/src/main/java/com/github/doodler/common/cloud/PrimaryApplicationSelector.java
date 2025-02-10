package com.github.doodler.common.cloud;

import java.util.Collection;

/**
 * 
 * @Description: PrimaryApplicationSelector
 * @Author: Fred Feng
 * @Date: 06/02/2025
 * @Version 1.0.0
 */
public interface PrimaryApplicationSelector {

    ApplicationInfo selectPrimary(Collection<ApplicationInfo> applicationInfos);

    default ApplicationInfo selectClusterPrimary(Collection<ApplicationInfo> applicationInfos) {
        return selectPrimary(applicationInfos);
    }

}
