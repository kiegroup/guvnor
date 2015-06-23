/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.client.navigator;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class CommitNavigator extends Composite {

    private FlowPanel container = new FlowPanel();
    private FlexTable navigator = null;
    private int lastIndex;
    private ParameterizedCommand<VersionRecord> onRevertCommand = null;

    public CommitNavigator() {
        initWidget( container );
    }

    public void setOnRevertCommand( final ParameterizedCommand<VersionRecord> command ) {
        this.onRevertCommand = command;
    }

    public void loadContent( final List<VersionRecord> versionRecords ) {
        lastIndex = 0;
        container.clear();
        if ( navigator != null ) {
            navigator.clear();
        }
        navigator = new FlexTable();
        navigator.setStyleName( NavigatorResources.INSTANCE.css().navigator() );

        setupContent( versionRecords );
    }

    public void addContent( final List<VersionRecord> content ) {
        int base = navigator.getRowCount();
        for ( int i = 0; i < content.size(); i++ ) {
            final VersionRecord dataContent = content.get( i );
            createElement( base + i, dataContent );
        }
    }

    private void setupContent( final List<VersionRecord> content ) {
        addContent( content );

        container.add( navigator );
    }

    private void createElement( final int row,
                                final VersionRecord dataContent ) {
        int col = 0;

        final Element messageCol = DOM.createDiv();
        messageCol.addClassName( NavigatorResources.INSTANCE.css().navigatorMessage() );
        {
            { //comment
                final Element message = DOM.createSpan();
                message.addClassName( NavigatorResources.INSTANCE.css().message() );
                message.setInnerText( dataContent.comment() );
                messageCol.appendChild( message );
            }

            final Element metadata = DOM.createDiv();

            {//author
                final Element author = DOM.createSpan();
                author.addClassName( NavigatorResources.INSTANCE.css().author() );
                author.setInnerText( dataContent.author() + " - " );
                metadata.appendChild( author );
            }

            {//date
                final Element date = DOM.createSpan();
                date.addClassName( NavigatorResources.INSTANCE.css().date() );
                DateTimeFormat fmt = DateTimeFormat.getFormat( "yyyy-MM-dd h:mm a" );
                date.setInnerText( fmt.format( dataContent.date() ) );
                metadata.appendChild( date );
            }

            messageCol.appendChild( metadata );
        }

        navigator.setWidget( row, col, new Widget() {{
            setElement( messageCol );
        }} );

        if ( onRevertCommand != null ) {
            navigator.setWidget( row, ++col, new Button( CoreConstants.INSTANCE.RevertToThis() ) {{
                setType( ButtonType.DANGER );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        final YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( CommonConstants.INSTANCE.Warning(),
                                                                                                        CoreConstants.INSTANCE.ConfirmStateRevert(),
                                                                                                        new Command() {
                                                                                                            @Override
                                                                                                            public void execute() {
                                                                                                                onRevertCommand.execute( dataContent );
                                                                                                            }
                                                                                                        },
                                                                                                        new Command() {
                                                                                                            @Override
                                                                                                            public void execute() {
                                                                                                            }
                                                                                                        },
                                                                                                        null
                                                                                                      );
                        yesNoCancelPopup.setCloseVisible( false );
                        yesNoCancelPopup.show();
                    }
                } );
            }} );
        }

        lastIndex++;
    }

    public int getLastIndex() {
        return lastIndex;
    }

}