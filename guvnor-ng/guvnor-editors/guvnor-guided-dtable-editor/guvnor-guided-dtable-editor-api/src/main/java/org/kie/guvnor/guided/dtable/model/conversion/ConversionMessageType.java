package org.kie.guvnor.guided.dtable.model.conversion;

/**
 * Types of message
 */

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum ConversionMessageType {
    INFO,
    WARNING,
    ERROR
}