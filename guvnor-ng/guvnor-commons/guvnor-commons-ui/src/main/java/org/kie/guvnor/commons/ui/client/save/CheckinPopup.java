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

package org.kie.guvnor.commons.ui.client.save;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.FormStylePopup;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * A popup and confirmation dialog for committing an asset.
 */
public class CheckinPopup extends FormStylePopup {

    private final TextArea    comment;
    private final Button      save;
    private final SaveCommand command;

    public CheckinPopup( final String message,
                         final SaveCommand command ) {
        this.command = checkNotNull( "command", command );

        setTitle( message );
        comment = new TextArea();
        comment.setWidth( "100%" );
        comment.setTitle( CommonConstants.INSTANCE.AddAnOptionalCheckInComment() );

        save = new Button( CommonConstants.INSTANCE.CheckIn() );
        save.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                hide();
                command.execute( getCheckinComment() );
            }
        } );

        addRow( comment );
        addRow( save );
    }

    public void show() {
        setAfterShow( new Command() {
            public void execute() {
                comment.setFocus( true );
            }
        } );
        super.show();
        comment.setFocus( true );
    }

    public String getCheckinComment() {
        return comment.getText();
    }

}
