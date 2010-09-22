/**
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


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Command;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RuleAsset;

/**
 * This holds the editor and viewer for rule documentation.
 * It will update the model when the text is changed.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends DirtyableComposite {

	private TextArea text;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public RuleDocumentWidget(final RuleAsset asset) {
        MetaData data = asset.metaData;
		text = new TextArea();
        text.setWidth( "90%" );
        text.setVisibleLines( 5 );
        text.setStyleName( "rule-viewer-Documentation" ); //NON-NLS
        text.setTitle(constants.RuleDocHint());

        DisclosurePanel p = new DisclosurePanel(
        		constants.Description() + ":" );
        p.setAnimationEnabled(true);
        p.addStyleName("my-DisclosurePanel");
        p.setWidth("100%");

        if (data.description == null || data.description.equals("") || data.description.equals("<documentation>")) {
            p.setOpen(true);
        }
        p.setContent(text);

        final VerticalPanel vp = new VerticalPanel();

        vp.add(p);

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                vp.add(new DiscussionWidget(asset));
            }
        });

        vp.setWidth("100%");

        loadData(data);

        initWidget(vp);
	}

    private void loadData(final MetaData data) {
        text.setText(data.description);
        text.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                data.description = text.getText();
                makeDirty();
            }
        });
        if (data.description == null || "".equals(data.description )) {
            text.setText(constants.documentationDefault());
        }
    }
}