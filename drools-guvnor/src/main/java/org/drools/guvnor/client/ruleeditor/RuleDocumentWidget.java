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



import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.rpc.MetaData;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * This holds the editor and viewer for rule documentation.
 * It will update the model when the text is changed.
 * @author Michael Neale
 *
 */
public class RuleDocumentWidget extends DirtyableComposite {

	private TextArea text;

	public RuleDocumentWidget(MetaData data) {
//
//        HorizontalPanel horiz = new HorizontalPanel();
//

		text = new TextArea();

        text.setWidth( "100%" );
        text.setVisibleLines( 5 );
        text.setStyleName( "rule-viewer-Documentation" );
        text.setTitle( "This is rule documentation. Human friendly descriptions of the business logic.");
		initWidget(text);
        loadData(data);
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
            text.setText( "<documentation>" );
        }
    }



}