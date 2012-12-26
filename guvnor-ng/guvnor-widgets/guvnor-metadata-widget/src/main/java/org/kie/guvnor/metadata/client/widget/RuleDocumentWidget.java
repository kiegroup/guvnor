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

package org.kie.guvnor.metadata.client.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.client.common.DirtyableComposite;

/**
 * This holds the editor and viewer for artifact documentation.
 * It will update the model when the text is changed.
 */
public class RuleDocumentWidget extends DirtyableComposite {

    private final VerticalPanel layout = new VerticalPanel();

    private final Metadata metadata;
    private       boolean  readOnly;

    public RuleDocumentWidget( final Metadata metadata,
                               boolean readOnly ) {
        this.metadata = metadata;
        this.readOnly = readOnly;

        initWidget( layout );
        initLayout();
    }

    private void initLayout() {
        layout.clear();

        layout.add( new CommentWidget( metadata, readOnly ) );

        Scheduler.get().scheduleDeferred( new Command() {
            public void execute() {
                layout.add( new DiscussionWidget( metadata, readOnly ) );
            }
        } );

        layout.setWidth( "100%" );
    }
}
