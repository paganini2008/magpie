package com.github.doodler.common.cloud.zookeeper;

import static com.github.doodler.common.cloud.redis.CloudConstants.METADATA_APPLICATION_INFO;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import com.github.doodler.common.cloud.AbstractDiscoveryClientRegistrar;
import com.github.doodler.common.cloud.ApplicationInfo;
import com.github.doodler.common.cloud.ApplicationInfoHolder;
import com.github.doodler.common.cloud.ApplicationInfoRegisteredEvent;
import com.github.doodler.common.cloud.DiscoveryClientProperties;
import com.github.doodler.common.utils.JacksonUtils;

/**
 * 
 * @Description: ZookeeperDiscoveryClientRegistrar
 * @Author: Fred Feng
 * @Date: 09/09/2024
 * @Version 1.0.0
 */
public class ZookeeperDiscoveryClientRegistrar extends AbstractDiscoveryClientRegistrar {

    public ZookeeperDiscoveryClientRegistrar(DiscoveryClientProperties config,
            ApplicationInfoHolder applicationInfoHolder) {
        super(config, applicationInfoHolder);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        super.onApplicationEvent(event);

        ServiceInstanceRegistration registration =
                event.getApplicationContext().getBean(ServiceInstanceRegistration.class);
        Map<String, String> metadata = registration.getMetadata();
        String appInfoString = metadata.get(METADATA_APPLICATION_INFO);
        if (StringUtils.isNotBlank(appInfoString)) {
            appInfoString = new String(Base64.decodeBase64(appInfoString));
            applicationEventPublisher.publishEvent(new ApplicationInfoRegisteredEvent(this,
                    JacksonUtils.parseJson(appInfoString, ApplicationInfo.class)));
        }
    }



}
