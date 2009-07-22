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
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.Panel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.MetaData;

/**
 * This holds the editor and viewer for rule documentation.
 * It will update the model when the text is changed.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends DirtyableComposite {

	private TextArea text;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public RuleDocumentWidget(MetaData data) {
		text = new TextArea();
        text.setWidth( "100%" );
        text.setVisibleLines( 5 );
        text.setStyleName( "rule-viewer-Documentation" ); //NON-NLS
        text.setTitle(constants.RuleDocHint());


        Panel p = new Panel();
        p.setCollapsible( true );
        p.setTitle( constants.Description() + ":" );
        p.setBodyBorder(false);


        if (data.description == null || data.description.equals("") || data.description.equals("<documentation>")) {
            p.setCollapsed(true);
        }
        p.add(text);
        



        Label lbl = new Label("This is a comment");
        lbl.setStyleName("x-form-field");








        VerticalPanel vp = new VerticalPanel();
        vp.add(p);


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