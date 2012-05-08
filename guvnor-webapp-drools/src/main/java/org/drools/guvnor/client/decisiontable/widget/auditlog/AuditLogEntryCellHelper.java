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
package org.drools.guvnor.client.decisiontable.widget.auditlog;

import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.auditlog.AuditLogEntry;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.ActionInsertFactColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.ActionSetFieldColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.AttributeColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.ColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.ConditionColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.DeleteColumnAuditLogEntry;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.DeleteRowAuditLogEntry;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.InsertColumnAuditLogEntry;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.InsertRowAuditLogEntry;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.LimitedEntryActionInsertFactColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.LimitedEntryActionSetFieldColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.LimitedEntryConditionColumnDetails;
import org.drools.ide.common.client.modeldriven.dt52.auditlog.MetadataColumnDetails;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Render different HTML for different AuditLogEvents
 */
public class AuditLogEntryCellHelper {

    /**
     * Lookup display text for each AuditLogEntry type
     * 
     * @param eventType
     * @return
     */
    public String getEventTypeDisplayText(final Class< ? extends AuditLogEntry> eventType) {
        if ( eventType.equals( InsertColumnAuditLogEntry.class ) ) {
            return Constants.INSTANCE.DecisionTableAuditLogEventInsertColumn();
        } else if ( eventType.equals( InsertRowAuditLogEntry.class ) ) {
            return Constants.INSTANCE.DecisionTableAuditLogEventInsertRow();
        } else if ( eventType.equals( DeleteColumnAuditLogEntry.class ) ) {
            return Constants.INSTANCE.DecisionTableAuditLogEventDeleteColumn();
        } else if ( eventType.equals( DeleteRowAuditLogEntry.class ) ) {
            return Constants.INSTANCE.DecisionTableAuditLogEventDeleteRow();
        }
        throw new IllegalArgumentException( "Unrecognised AuditLogEntry type." );
    }

    public SafeHtml getSafeHtml(final AuditLogEntry event) {
        if ( event instanceof InsertColumnAuditLogEntry ) {
            return getSafeHtml( (InsertColumnAuditLogEntry) event );
        } else if ( event instanceof DeleteColumnAuditLogEntry ) {
            return getSafeHtml( (DeleteColumnAuditLogEntry) event );
        } else if ( event instanceof InsertRowAuditLogEntry ) {
            return getSafeHtml( (InsertRowAuditLogEntry) event );
        } else if ( event instanceof DeleteRowAuditLogEntry ) {
            return getSafeHtml( (DeleteRowAuditLogEntry) event );
        }
        throw new IllegalArgumentException( "Unrecognised AuditLogEntry type." );
    }

    private SafeHtml getSafeHtml(final InsertRowAuditLogEntry event) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertRowAt0( event.getRowIndex() + 1 ) );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml(final DeleteRowAuditLogEntry event) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogDeleteRowAt0( event.getRowIndex() + 1 ) );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml(final InsertColumnAuditLogEntry event) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        buildColumnDetails( event.getDetails(),
                            sb );
        return sb.toSafeHtml();
    }

    private SafeHtml getSafeHtml(final DeleteColumnAuditLogEntry event) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogDeleteColumn0( event.getColumnHeader() ) );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
        return sb.toSafeHtml();
    }

    private void buildColumnDetails(final ColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        if ( details instanceof AttributeColumnDetails ) {
            buildColumnDetails( (AttributeColumnDetails) details,
                                sb );
        } else if ( details instanceof MetadataColumnDetails ) {
            buildColumnDetails( (MetadataColumnDetails) details,
                                sb );
        } else if ( details instanceof ConditionColumnDetails ) {
            buildColumnDetails( (ConditionColumnDetails) details,
                                sb );
        } else if ( details instanceof LimitedEntryConditionColumnDetails ) {
            buildColumnDetails( (LimitedEntryConditionColumnDetails) details,
                                sb );
        } else if ( details instanceof ActionInsertFactColumnDetails ) {
            buildColumnDetails( (ActionInsertFactColumnDetails) details,
                                sb );
        } else if ( details instanceof LimitedEntryActionInsertFactColumnDetails ) {
            buildColumnDetails( (LimitedEntryActionInsertFactColumnDetails) details,
                                sb );
        } else if ( details instanceof ActionSetFieldColumnDetails ) {
            buildColumnDetails( (ActionSetFieldColumnDetails) details,
                                sb );
        } else if ( details instanceof LimitedEntryActionSetFieldColumnDetails ) {
            buildColumnDetails( (LimitedEntryActionSetFieldColumnDetails) details,
                                sb );
        } else {
            sb.appendHtmlConstant( "<table>" );
            sb.appendHtmlConstant( "<tr><td>" );
            sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertColumn0( details.getColumnHeader() ) );
            sb.appendHtmlConstant( "</td></tr>" );
            sb.appendHtmlConstant( "</table>" );
        }
    }

    private void buildColumnDetails(final AttributeColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertAttribute0( details.getAttribute() ) );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final MetadataColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertMetadata0( details.getMetadata() ) );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final ConditionColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertCondition0( details.getColumnHeader() ) );
        sb.appendEscaped( details.getFactField() );
        sb.appendEscaped( details.getOperator() );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final LimitedEntryConditionColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertCondition0( details.getColumnHeader() ) );
        sb.appendEscaped( details.getFactField() );
        sb.appendEscaped( details.getOperator() );
        sb.appendEscaped( details.getValue().toString() );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final ActionInsertFactColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertActionInsertFact0( details.getColumnHeader() ) );
        sb.appendEscaped( details.getFactType() );
        sb.appendEscaped( details.getFactField() );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final LimitedEntryActionInsertFactColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertActionInsertFact0( details.getColumnHeader() ) );
        sb.appendEscaped( details.getFactType() );
        sb.appendEscaped( details.getFactField() );
        sb.appendEscaped( details.getValue().toString() );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final ActionSetFieldColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertActionSetField0( details.getColumnHeader() ) );
        sb.appendEscaped( details.getBoundName() );
        sb.appendEscaped( details.getFactField() );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

    private void buildColumnDetails(final LimitedEntryActionSetFieldColumnDetails details,
                                    final SafeHtmlBuilder sb) {
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.DecisionTableAuditLogInsertActionSetField0( details.getColumnHeader() ) );
        sb.appendEscaped( details.getBoundName() );
        sb.appendEscaped( details.getFactField() );
        sb.appendEscaped( details.getValue().toString() );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );
    }

}
