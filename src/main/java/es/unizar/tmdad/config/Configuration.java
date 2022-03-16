package es.unizar.tmdad.config;

import es.unizar.tmdad.listener.MessageListener;
import es.unizar.tmdad.mapper.MessageMapper;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public MessageListener incomingMessagePcs(MessageMapper messageMapper){
        return new MessageListener(messageMapper);
    }

    @Bean
    public MessageListener reprocessMessagePcs(MessageMapper messageMapper){
        return new MessageListener(messageMapper);
    }

}
