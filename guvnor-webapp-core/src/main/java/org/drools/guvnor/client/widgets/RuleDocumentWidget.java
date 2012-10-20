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

package org.drools.guvnor.client.widgets;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Artifact;
import org.drools.guvnor.client.rpc.Asset;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This holds the editor and viewer for artifact documentation.
 * It will update the model when the text is changed.
 */
public class RuleDocumentWidget extends DirtyableComposite {

    final VerticalPanel     layout = new VerticalPanel();

    private final Artifact artifact;
    private boolean         readOnly;
    private final ClientFactory clientFactory;

    public RuleDocumentWidget(final Artifact artifact,
                              boolean readOnly,
                              ClientFactory clientFactory) {
        this.artifact = artifact;
        this.readOnly = readOnly;
        this.clientFactory = clientFactory;

        initWidget( layout );
        initLayout();
    }

    private void initLayout() {
        layout.clear();

        layout.add( new CommentWidget( artifact, readOnly ) );

		if (artifact instanceof Asset) {
	        Scheduler.get().scheduleDeferred( new Command() {
	            public void execute() {
	                layout.add( new DiscussionWidget( artifact, readOnly, clientFactory ) );
	            }
	        } );
		} 

        layout.setWidth( "100%" );
    }
}
