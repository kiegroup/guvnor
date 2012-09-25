package org.drools.guvnor.client.asseteditor;

/**
 * An Event to show a message in the Notification area
 * TODO: Deprecated, Uberfire has notifications for this -Rikkola-
 */
public class ShowMessageEvent {

    private final String message;

    private final MessageType messageType;

    public ShowMessageEvent(String message,
                            MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public static enum MessageType {
        INFO,
        WARNING,
        ERROR
    }
}
