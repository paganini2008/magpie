package com.github.doodler.common.cloud.eureka;

import static com.github.doodler.common.cloud.redis.CloudConstants.METADATA_APPLICATION_INFO;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import com.github.doodler.common.cloud.AbstractDiscoveryClientRegistrar;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoRegisteredEvent;
import com.github.doodler.common.cloud.DiscoveryClientProperties;
import com.github.doodler.common.utils.JacksonUtils;
import com.netflix.appinfo.ApplicationInfoManager;

/**
 * 
 * @Description: EurekaDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 02/05/2024
 * @Version 1.0.0
 */
public class EurekaDiscoveryClientRegistrar extends AbstractDiscoveryClientRegistrar {

    public EurekaDiscoveryClientRegistrar(DiscoveryClientProperties config,
            ApplicationInfoHolder applicationInfoHolder) {
        super(config, applicationInfoHolder);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        super.onApplicationEvent(event);

        ApplicationInfoManager appInfoManager =
                event.getApplicationContext().getBean(ApplicationInfoManager.class);
        Map<String, String> metadata = appInfoManager.getInfo().getMetadata();
        String json = metadata.get(METADATA_APPLICATION_INFO);
        if (StringUtils.isNotBlank(json)) {
            applicationEventPublisher.publishEvent(new ApplicationInfoRegisteredEvent(this,
                    JacksonUtils.parseJson(json, ApplicationInfo.class)));
        }
    }



}
