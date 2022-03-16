package es.unizar.tmdad.listener;

import es.unizar.tmdad.adt.Message;
import es.unizar.tmdad.adt.MessageIn;
import es.unizar.tmdad.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
public class MessageListener implements Function<Flux<MessageIn>, Flux<Message>> {

    private final MessageMapper messageMapper;

    public MessageListener(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    private MessageIn logs(MessageIn in){
        log.info("Processing msg {}.", in);
        return in;
    }

    @Override
    public Flux<Message> apply(Flux<MessageIn> messageInFlux) {
        return messageInFlux.map(this::logs).map(messageMapper::mapMessage);
    }
}
