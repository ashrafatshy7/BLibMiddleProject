package message;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L; // רצוי למנוע אזהרות

    private MessageType messageType;
    private Object data;

    public Message(MessageType messageType, Object data) {
        this.messageType = messageType;
        this.data = data;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Object getData() {
        return data;
    }
}
