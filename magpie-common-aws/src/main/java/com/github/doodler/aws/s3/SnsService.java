package com.github.doodler.aws.s3;

import java.util.Map;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: SnsService
 * @Author: Fred Feng
 * @Date: 07/01/2025
 * @Version 1.0.0
 */
@SuppressWarnings("all")
@RequiredArgsConstructor
public class SnsService {

    private final SnsTemplate snsTemplate;

    public <T> void sendMessage(String topic, T message, Map<String, Object> headers,
            MessageHandler<T> messageHandler) {
        if (messageHandler == null) {
            snsTemplate.convertAndSend(topic, message, headers);
        } else {

            snsTemplate.convertAndSend(topic, message, headers, m -> {
                T t = (T) snsTemplate.getMessageConverter().fromMessage(m, message.getClass());
                messageHandler.postHandleMessage(m.getHeaders(), m);
                return m;
            });
        }
    }

}
