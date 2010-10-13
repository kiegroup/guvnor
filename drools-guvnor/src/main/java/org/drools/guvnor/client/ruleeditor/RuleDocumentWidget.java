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

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This holds the editor and viewer for rule documentation.
 * It will update the model when the text is changed.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends DirtyableComposite {

    final VerticalPanel layout = new VerticalPanel();

    public RuleDocumentWidget(final RuleAsset asset) {

        layout.add( new CommentWidget( asset.metaData ) );

        DeferredCommand.addCommand( new Command() {
            public void execute() {
                layout.add( new DiscussionWidget( asset ) );
            }
        } );

        layout.setWidth( "100%" );

        initWidget( layout );
    }

}