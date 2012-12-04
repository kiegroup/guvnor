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

package org.kie.guvnor.projecteditor.client;

import org.kie.guvnor.projecteditor.model.builder.Message;
import org.kie.guvnor.projecteditor.model.builder.Messages;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MessageService {

    private List<Message> messageLog = new ArrayList<Message>();

    public void addMessages(Messages messages) {
        // TODO -Rikkola-
        //
//        placeManager.goTo("org.kie.guvnor.Messages");
    }

    public void onNewMessage(@Observes Message message) {
        // TODO -Rikkola-
    }

    public List<Message> getMessageLog() {
        return messageLog;
    }
}
