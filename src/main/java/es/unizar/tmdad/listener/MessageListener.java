package es.unizar.tmdad.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.tmdad.adt.MessageList;
import es.unizar.tmdad.adt.MessageListIn;
import es.unizar.tmdad.mapper.MessageMapper;
import es.unizar.tmdad.service.CounterService;
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
    private final CounterService counterService;

    @Value("${chat.exchanges.output}")
    private String topicExchangeName;

    public MessageListener(MessageMapper messageMapper, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, CounterService counterService) {
        this.messageMapper = messageMapper;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.counterService = counterService;
    }

    public void apply(String input) throws JsonProcessingException {
        this.counterService.newMessageReceived(input);
        MessageListIn msg = objectMapper.readValue(input, MessageListIn.class);
        log.info("Processing msg {}.", msg);
        MessageList output = messageMapper.mapMessage(msg);

        String messageAsString = this.objectMapper.writeValueAsString(output);
        this.counterService.newMessageSent(messageAsString);
        this.rabbitTemplate.convertAndSend(topicExchangeName, "", messageAsString);
    }
}
