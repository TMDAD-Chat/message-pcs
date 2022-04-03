package es.unizar.tmdad.mapper;

import es.unizar.tmdad.adt.MessageList;
import es.unizar.tmdad.adt.MessageListIn;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageList mapMessage(MessageListIn msg);

}
