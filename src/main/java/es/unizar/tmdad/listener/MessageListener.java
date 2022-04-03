package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.Message;
import es.unizar.tmdad.adt.MessageIn;
import es.unizar.tmdad.adt.MessageList;
import es.unizar.tmdad.adt.MessageListIn;
import es.unizar.tmdad.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener {

    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${chat.exchanges.output:message-pcs}")
    private String topicExchangeName;

    public MessageListener(MessageMapper messageMapper, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.messageMapper = messageMapper;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void apply(String input) throws JsonProcessingException {
        MessageListIn msg = objectMapper.readValue(input, MessageListIn.class);
        MessageList output = this.apply(msg);
        this.rabbitTemplate.convertAndSend(topicExchangeName, "", this.objectMapper.writeValueAsString(output));
    }

    public MessageList apply(MessageListIn messageInFlux) {
        log.info("Processing msg {}.", messageInFlux);
        return messageMapper.mapMessage(messageInFlux);
    }
}
