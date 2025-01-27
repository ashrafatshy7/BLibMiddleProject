package message;

import java.io.Serializable;

/**
 * Represents a message that can be sent between different parts of the application.
 * Implements Serializable to allow the object to be transmitted over a network or saved to a file.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The type of the message, defining the purpose or category of the message. */
    private MessageType msgType;

    /** The data associated with the message, which can be of any object type. */
    private Object msgData;

    /**
     * Constructs a Message object with a specified message type and message data.
     *
     * @param messageType The type of the message.
     * @param messageData The data associated with the message.
     */
    public Message(MessageType messageType, Object messageData) {
        this.msgType = messageType;
        this.msgData = messageData;
    }

    /**
     * Constructs a Message object with a specified message type only.
     *
     * @param messageType The type of the message.
     */
    public Message(MessageType messageType) {
        this.msgType = messageType;
    }

    /**
     * Retrieves the message type.
     *
     * @return The message type.
     */
    public MessageType getMessageType() {
        return msgType;
    }

    /**
     * Retrieves the message data.
     *
     * @return The message data as an Object.
     */
    public Object getMessageData() {
        return msgData;
    }

    /**
     * Provides a string representation of the message object.
     *
     * @return A string representation of the message.
     */
    @Override
    public String toString() {
        return "MESSAGE";
    }
}
