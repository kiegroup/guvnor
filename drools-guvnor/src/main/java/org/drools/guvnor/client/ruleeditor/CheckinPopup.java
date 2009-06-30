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



import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;

/**
 *
 * A popup and confirmation dialog for committing an asset.
 *
 * @author Michael Neale
 *
 */
public class CheckinPopup {


    private TextArea comment;
    private Button save;
    private FormStylePopup pop;
    private Constants constants = ((Constants) GWT.create(Constants.class));


    public CheckinPopup(int left, int top, String message) {
        pop = new FormStylePopup();
        pop.setTitle(message);
        comment = new TextArea();
        comment.setWidth( "100%" );
        comment.setTitle(constants.AddAnOptionalCheckInComment());

        save = new Button(constants.CheckIn());
        pop.addRow(comment);
        pop.addRow(save);

    }

    public void setCommand(final Command checkin) {
    	final ClickListener cl = new ClickListener() {
            public void onClick(Widget w) {
                checkin.execute();
                pop.hide();
            }
        };
        save.addClickListener( cl );
        comment.addKeyboardListener(new KeyboardListenerAdapter() {
        	@Override
        	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
        		if (keyCode == KeyboardListener.KEY_ENTER) {
        			cl.onClick(null);
        		}
        	}
        });
    }

    public void show() {
        pop.setAfterShow(new Command() {
            public void execute() {
                comment.setFocus(true);
            }
        });
		pop.show();
        comment.setFocus(true);

    }

    public String getCheckinComment() {
        return comment.getText();
    }

}