package com.github.doodler.common.events;

import com.github.doodler.common.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: OnlineGlobalApplicationEvent
 * @Author: Fred Feng
 * @Date: 05/01/2025
 * @Version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class OnlineGlobalApplicationEvent extends GlobalApplicationEvent {

    public OnlineGlobalApplicationEvent(String serviceId, Object source) {
        super(source);
        this.serviceId = serviceId;
        this.name = Constants.GLOBAL_APPLICATION_EVENT_ONLINE;
    }

    private String serviceId;
    private String name;

    @Override
    public String getName() {
        return name;
    }

}
