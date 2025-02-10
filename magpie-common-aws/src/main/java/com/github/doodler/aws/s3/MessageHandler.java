package com.github.doodler.aws.s3;

import java.util.Map;
import org.springframework.messaging.Message;

/**
 * 
 * @Description: MessageHandler
 * @Author: Fred Feng
 * @Date: 07/01/2025
 * @Version 1.0.0
 */
public interface MessageHandler<T> {

    void postHandleMessage(Map<String, Object> headers, Message<?> message);

}
