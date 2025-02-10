package com.github.doodler.aws.s3;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: SqsService
 * @Author: Fred Feng
 * @Date: 07/01/2025
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class SqsService {

    private final SqsTemplate sqsTemplate;

    public void sendMessage(String queueName, Object obj) throws IOException {
        sqsTemplate.send(queueName, obj);
    }

    public void sendsendMessageAsync(String queueName, Object obj) throws IOException {
        sqsTemplate.sendAsync(queueName, obj);
    }

    public void sendManysendMessages(String queueName, Collection<Object> c) throws IOException {
        List<Message<Object>> messages =
                c.stream().map(o -> MessageBuilder.withPayload(o).build()).toList();
        sqsTemplate.sendMany(queueName, messages);
    }

    public void sendManysendMessagesAsync(String queueName, Collection<Object> c)
            throws IOException {
        List<Message<Object>> messages =
                c.stream().map(o -> MessageBuilder.withPayload(o).build()).toList();
        sqsTemplate.sendManyAsync(queueName, messages);
    }

    public <T> T receiveMessage(String queueName, Class<T> messageType) {
        Message<T> message = sqsTemplate.receive(queueName, messageType).orElse(null);
        return message != null ? message.getPayload() : null;
    }

    public <T> Collection<T> receiveMessages(String queueName, Class<T> messageType) {
        Collection<Message<T>> messages = sqsTemplate.receiveMany(queueName, messageType);
        return messages != null ? messages.stream().map(m -> m.getPayload()).toList()
                : Collections.emptyList();
    }

    public <T> void receiveMessageAsync(String queueName, Class<T> messageType,
            MessageHandler<T> handler) {
        CompletableFuture<Optional<Message<T>>> message =
                sqsTemplate.receiveAsync(queueName, messageType);
        message.thenAccept(o -> {
            o.ifPresent(m -> handler.postHandleMessage(m.getHeaders(), m));
        });
    }

    public <T> void receiveMessagesAsync(String queueName, Class<T> messageType,
            MessageHandler<T> handler) {
        CompletableFuture<Collection<Message<T>>> messages =
                sqsTemplate.receiveManyAsync(queueName, messageType);
        messages.thenAccept(c -> {
            c.forEach(m -> handler.postHandleMessage(m.getHeaders(), m));
        });
    }

}
