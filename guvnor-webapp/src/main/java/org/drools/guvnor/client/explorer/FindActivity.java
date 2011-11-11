/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.widgets.query.QueryWidget;

public class FindActivity extends Activity {

    private Constants constants = GWT.create( Constants.class );
    private final ClientFactory clientFactory;

    public FindActivity( ClientFactory clientFactory ) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start( AcceptItem tabbedPanel, EventBus eventBus ) {
        tabbedPanel.add(
                constants.Find(),
                new QueryWidget( clientFactory )
        );
    }
}
