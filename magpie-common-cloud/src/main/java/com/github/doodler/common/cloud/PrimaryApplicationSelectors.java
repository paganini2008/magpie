package com.github.doodler.common.cloud;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @Description: PrimaryApplicationSelectors
 * @Author: Fred Feng
 * @Date: 06/02/2025
 * @Version 1.0.0
 */
public abstract class PrimaryApplicationSelectors {

    public static PrimaryApplicationSelector firstApplication() {
        return new PrimaryApplicationSelector() {

            @Override
            public ApplicationInfo selectPrimary(Collection<ApplicationInfo> applicationInfos) {
                for (Iterator<ApplicationInfo> it = applicationInfos.iterator(); it.hasNext();) {
                    return it.next();
                }
                return null;
            }
        };
    }

    public static PrimaryApplicationSelector lastApplication() {
        return new PrimaryApplicationSelector() {

            @Override
            public ApplicationInfo selectPrimary(Collection<ApplicationInfo> applicationInfos) {
                ApplicationInfo appInfo = null;
                for (Iterator<ApplicationInfo> it = applicationInfos.iterator(); it.hasNext();) {
                    appInfo = it.next();
                }
                return appInfo;
            }
        };
    }
}
