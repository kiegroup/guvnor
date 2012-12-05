package org.kie.guvnor.guided.dtable.model.auditlog;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Events recorded for the Decision Table audit log
 */
@Portable
//DO NOT CHANGE THE NAMES OF THESE ENUMS TO PRESERVE COMPATIBILITY OF EXISTING AUDIT LOGS IN FUTURE RELEASES
public enum DecisionTableAuditEvents {
    INSERT_ROW,
    INSERT_COLUMN,
    DELETE_ROW,
    DELETE_COLUMN,
    UPDATE_COLUMN
}