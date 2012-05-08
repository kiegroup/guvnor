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

import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.auditlog.AuditLogEntry;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A cell to render AuditLogEntry's
 */
public class AuditLogEntryCell extends AbstractCell<AuditLogEntry> {

    private static final String           DATE_TIME_FORMAT = ApplicationPreferences.getDroolsDateTimeFormat();

    private static final DateTimeFormat   format           = DateTimeFormat.getFormat( DATE_TIME_FORMAT );

    private final AuditLogEntryCellHelper renderer;

    public AuditLogEntryCell(final AuditLogEntryCellHelper renderer) {
        this.renderer = renderer;
    }

    @Override
    public void render(Context context,
                       AuditLogEntry value,
                       SafeHtmlBuilder sb) {
        if ( value == null ) {
            return;
        }

        //Audit Log entry type and date
        sb.appendHtmlConstant( "<table>" );
        sb.appendHtmlConstant( "<tr><td><b>" );
        sb.appendEscaped( renderer.getEventTypeDisplayText( value.getClass() ) );
        sb.appendHtmlConstant( "</b></td></tr>" );
        sb.appendHtmlConstant( "<tr><td>" );
        sb.appendEscaped( Constants.INSTANCE.AuditLogEntryBy0On1( value.getUserName(),
                                                                  format.format( value.getDateOfEntry() ) ) );
        sb.appendHtmlConstant( "</td></tr>" );
        sb.appendHtmlConstant( "</table>" );

        //Audit Log entry detail
        sb.append( renderer.getSafeHtml( value ) );
    }

}
