package es.unizar.tmdad.adt;

import lombok.Data;

import java.util.List;

@Data
public class MessageList {

    private String requestId;
    private String recipient;
    private RecipientType recipientType;
    private List<Message> messages;

}
