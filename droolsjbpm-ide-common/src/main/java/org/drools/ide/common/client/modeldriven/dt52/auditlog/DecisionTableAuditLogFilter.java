/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.ide.common.client.modeldriven.dt52.auditlog;

import org.drools.ide.common.client.modeldriven.auditlog.AuditLogFilter;

/**
 * An AuditLogFilter implementation specific to the types of AuditLogEntry's
 * created by a Web Guided Decision Table.
 */
public class DecisionTableAuditLogFilter extends AuditLogFilter {

    private static final long serialVersionUID = 8506440541322969289L;

    public DecisionTableAuditLogFilter() {
        addType( DecisionTableAuditEvents.INSERT_ROW.name() );
        addType( DecisionTableAuditEvents.INSERT_COLUMN.name() );
        addType( DecisionTableAuditEvents.DELETE_ROW.name() );
        addType( DecisionTableAuditEvents.DELETE_COLUMN.name() );
        addType( DecisionTableAuditEvents.UPDATE_COLUMN.name() );
    }

    //DO NOT CHANGE THE NAMES OF THESE ENUMS TO PRESERVE COMPATIBILITY OF EXISTING AUDIT LOGS IN FUTURE RELEASES
    public static enum DecisionTableAuditEvents {
        INSERT_ROW,
        INSERT_COLUMN,
        DELETE_ROW,
        DELETE_COLUMN,
        UPDATE_COLUMN
    }

}
