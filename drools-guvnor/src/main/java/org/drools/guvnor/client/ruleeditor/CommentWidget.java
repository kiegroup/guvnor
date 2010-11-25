/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * 
 * @author rikkola
 *
 */
public class CommentWidget extends DirtyableComposite {

    private Constants                      constants = GWT.create( Constants.class );

    private final TextArea                 text;
    private final DecoratedDisclosurePanel disclosurePanel;

    public CommentWidget(final MetaData data) {
        text = getTextArea();
        disclosurePanel = getDisclosurePanel();

        disclosurePanel.setContent( text );

        disclosurePanel.addOpenHandler( new OpenHandler<DisclosurePanel>() {
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                loadData( data );
            }
        } );

        if ( isDescriptionUnSet( data ) ) {
            disclosurePanel.setOpen( true );
        }

        initWidget( disclosurePanel );

    }

    private DecoratedDisclosurePanel getDisclosurePanel() {
        DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel( constants.Description() );
        disclosurePanel.setWidth( "100%" );
        return disclosurePanel;
    }

    private TextArea getTextArea() {
        TextArea text = new TextArea();
        text.setWidth( "90%" );
        text.setVisibleLines( 5 );
        text.setStyleName( "rule-viewer-Documentation" ); //NON-NLS
        text.setTitle( constants.RuleDocHint() );
        return text;
    }

    private boolean isDescriptionUnSet(MetaData data) {
        return data.description == null || data.description.equals( "" ) || data.description.equals( "<documentation>" );
    }

    private void loadData(final MetaData data) {
        text.setText( data.description );
        text.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                data.description = text.getText();
                makeDirty();
            }
        } );
        if ( data.description == null || "".equals( data.description ) ) {
            text.setText( constants.documentationDefault() );
        }
    }
}
