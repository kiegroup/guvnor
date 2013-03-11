/*
 * Copyright 2005 JBoss Inc
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

package org.kie.guvnor.commons.ui.client.popups.file;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.kie.guvnor.commons.ui.client.resources.CommonImages;
import org.uberfire.client.common.FormStylePopup;

import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

public class RenamePopup extends FormStylePopup {

    final private TextBox nameTextBox = new TextBox();
    final private TextBox checkInCommentTextBox = new TextBox();

    public RenamePopup( final CommandWithFileNameAndCommitMessage command ) {
        super( CommonImages.INSTANCE.edit(),
               "Rename this item" );

        checkNotNull( "command",
                      command );

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );
        setGlassEnabled( true );

        nameTextBox.setTitle( "New name" );
        nameTextBox.setWidth( "200px" );
        addAttribute( "New name:", nameTextBox );

        checkInCommentTextBox.setTitle( "Check in comment" );
        checkInCommentTextBox.setWidth( "200px" );
        addAttribute( "Check in comment:",
                      checkInCommentTextBox );

        final HorizontalPanel hp = new HorizontalPanel();
        final Button create = new Button( "Rename item" );
        create.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent arg0 ) {

                if ( nameTextBox.getText() == null || "".equals( nameTextBox.getText() ) ) {
                    Window.alert( "Please enter the name you would like to change this asset to" );
                    return;
                }

                if ( !Window.confirm( "Are you sure you want to rename this asset to " + nameTextBox.getText() ) ) {
                    return;
                }

                hide();
                command.execute( new FileNameAndCommitMessage( nameTextBox.getText(),
                                                     checkInCommentTextBox.getText() ) );
            }
        } );
        hp.add( create );

        Button cancel = new Button( "Cancel" );
        cancel.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent arg0 ) {
                hide();
            }
        } );
        hp.add( new HTML( "&nbsp" ) );
        hp.add( cancel );
        addAttribute( "", hp );

    }

}
