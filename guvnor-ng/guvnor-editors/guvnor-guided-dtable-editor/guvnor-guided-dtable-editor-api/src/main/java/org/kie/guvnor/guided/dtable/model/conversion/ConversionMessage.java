package org.kie.guvnor.guided.dtable.model.conversion;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A message resulting from the conversion process
 */
@Portable
public class ConversionMessage {

    private static final long serialVersionUID = 540L;

    private String message;

    private ConversionMessageType messageType;

    public ConversionMessage() {
    }

    public ConversionMessage( String message,
                              ConversionMessageType messageType ) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return this.message;
    }

    public ConversionMessageType getMessageType() {
        return this.messageType;
    }

}