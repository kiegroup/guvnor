/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.client.screens;

import java.util.Date;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
@WorkbenchScreen( identifier = "MessageCreatorScreen" )
public class MessageCreatorScreen extends Composite {

    private FlowPanel panel = new FlowPanel();

    @Inject
    private Event<PublishMessagesEvent> systemMessageEvent;

    @PostConstruct
    public void init() {
        final ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add( newMessageButton( Level.INFO ) );
        btnGroup.add( newMessageButton( Level.ERROR ) );
        btnGroup.add( newMessageButton( Level.WARNING ) );

        panel.add( btnGroup );
        initWidget( panel );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Messages";
    }

    private Button newMessageButton( final Level level ){
        final Button add = new Button( level.name() + " message" );
        add.setIcon( IconType.PLUS );
        add.addClickHandler( new NewMessageClickHandler( level ) );
        return add;
    }


    private class NewMessageClickHandler implements ClickHandler {

        private Level level;

        public NewMessageClickHandler( final Level level ) {
            this.level = level;
        }

        @Override
        public void onClick( final ClickEvent event ) {
            try {
                final SystemMessage systemMessage = new SystemMessage();
                systemMessage.setText( "System demo message at " + new Date() );
                systemMessage.setLevel( level );
                systemMessage.setPath( PathFactory.newPath( "system", "git" ) );
                systemMessage.setColumn( new Random().nextInt() );
                systemMessage.setId( new Random().nextInt() );
                systemMessage.setLine( new Random().nextInt() );

                final PublishMessagesEvent message = new PublishMessagesEvent();
                message.getMessagesToPublish().add( systemMessage );
                systemMessageEvent.fire( message );
            } catch ( Exception e ) {
                ErrorPopup.showMessage( e.getMessage() );
            }

        }

    }

}
