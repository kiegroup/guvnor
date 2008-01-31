package org.drools.brms.client.ruleeditor;
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



import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.FormStylePopup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

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


    public CheckinPopup(int left, int top, String message) {
        pop = new FormStylePopup("images/checkin.gif", message);
        comment = new TextArea();
        comment.setWidth( "100%" );
        save = new Button("Save");
        pop.addAttribute( "Comment", comment );
        pop.addAttribute( "", save);

    }

    public void setCommand(final Command checkin) {
        save.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                checkin.execute();
                pop.hide();
            }
        });
    }

    public void show() {
		pop.show();
    }

    public String getCheckinComment() {
        return comment.getText();
    }

}