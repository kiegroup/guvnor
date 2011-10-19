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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;

/**
 * A popup and confirmation dialog for committing an asset.
 */
public class CheckinPopup extends FormStylePopup {

    private TextArea comment;
    private Button save;

    private Command checkin;

    public CheckinPopup(String message) {
        setTitle(message);
        comment = new TextArea();
        comment.setWidth("100%");
        Constants constants = ((Constants) GWT.create(Constants.class));
        comment.setTitle(constants.AddAnOptionalCheckInComment());

        save = new Button(constants.CheckIn());
        addRow(comment);
        addRow(save);

    }

    public void setCommand(final Command checkin) {
        this.checkin = checkin;

        save.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                checkIn();
            }
        });

        comment.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    checkIn();
                }
            }
        });
    }

    private void checkIn() {
        checkin.execute();
        hide();
    }

    public void show() {
        setAfterShow(new Command() {
            public void execute() {
                comment.setFocus(true);
            }
        });
        super.show();
        comment.setFocus(true);

    }

    public String getCheckinComment() {
        return comment.getText();
    }

}
