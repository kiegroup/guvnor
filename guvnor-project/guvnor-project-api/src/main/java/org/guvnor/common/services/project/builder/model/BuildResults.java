/*
 * Copyright 2012 JBoss Inc
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

package org.guvnor.common.services.project.builder.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class BuildResults {

    private GAV gav;
    private ArrayList<BuildMessage> messages = new ArrayList<BuildMessage>();

    public BuildResults() {
        //Marshalling
    }

    public BuildResults( final GAV gav ) {
        this.gav = gav;
    }

    public GAV getGAV() {
        return gav;
    }

    public List<BuildMessage> getMessages() {
        return Collections.unmodifiableList( messages );
    }

    public List<BuildMessage> getErrorMessages() {
        return Collections.unmodifiableList( filterMessages( BuildMessage.Level.ERROR ) );
    }

    public List<BuildMessage> getWarningMessages() {
        return Collections.unmodifiableList( filterMessages( BuildMessage.Level.WARNING ) );
    }

    public List<BuildMessage> getInformationMessages() {
        return Collections.unmodifiableList( filterMessages( BuildMessage.Level.INFO ) );
    }

    private List<BuildMessage> filterMessages( final BuildMessage.Level level ) {
        final List<BuildMessage> filteredMessages = new ArrayList<BuildMessage>();
        for ( BuildMessage msg : messages ) {
            if ( msg.getLevel() == level ) {
                filteredMessages.add( msg );
            }
        }
        return filteredMessages;
    }

    public void addBuildMessage( final BuildMessage message ) {
        this.messages.add( message );
    }

    public void addBuildMessage( final int index, final BuildMessage message ) {
        this.messages.add( index, message );
    }

}
