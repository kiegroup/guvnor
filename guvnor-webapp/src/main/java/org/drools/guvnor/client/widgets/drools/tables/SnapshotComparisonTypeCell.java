/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.client.widgets.drools.tables;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.SnapshotDiff;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Cell to render the appropriate text and style for a Snapshot Comparison type
 */
public class SnapshotComparisonTypeCell extends AbstractCell<String> {

    private static Constants constants = GWT.create( Constants.class );

    interface Template
        extends
        SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml comparisonType(String className,
                                String value);
    }

    private static Template template = GWT.create( Template.class );

    @Override
    public void render(Context context,
                       String value,
                       SafeHtmlBuilder sb) {
        sb.append( template.comparisonType( getClassName( value ),
                                            getTextToRender( value ) ) );
    }

    private String getTextToRender(String value) {
        if ( value.equals( SnapshotDiff.TYPE_ADDED ) ) {
            return constants.TypeAdded();
        } else if ( value.equals( SnapshotDiff.TYPE_ARCHIVED ) ) {
            return constants.TypeArchived();
        } else if ( value.equals( SnapshotDiff.TYPE_DELETED ) ) {
            return constants.TypeDeleted();
        } else if ( value.equals( SnapshotDiff.TYPE_RESTORED ) ) {
            return constants.TypeRestored();
        } else if ( value.equals( SnapshotDiff.TYPE_UPDATED ) ) {
            return constants.TypeUpdated();
        }
        throw new IllegalArgumentException( "value of SnapshotComparison Type is unknown" );
    }

    private String getClassName(String value) {
        if ( value.equals( SnapshotDiff.TYPE_ADDED ) ) {
            return "snapshot-comparison-added";
        } else if ( value.equals( SnapshotDiff.TYPE_ARCHIVED ) ) {
            return "snapshot-comparison-archived";
        } else if ( value.equals( SnapshotDiff.TYPE_DELETED ) ) {
            return "snapshot-comparison-deleted";
        } else if ( value.equals( SnapshotDiff.TYPE_RESTORED ) ) {
            return "snapshot-comparison-restored";
        } else if ( value.equals( SnapshotDiff.TYPE_UPDATED ) ) {
            return "snapshot-comparison-updated";
        }
        throw new IllegalArgumentException( "value of SnapshotComparison Type is unknown" );
    }

}
