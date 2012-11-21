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

package org.kie.uberfirebootstrap.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.kie.uberfirebootstrap.client.resources.constants.UberfireBootstrapConstants;
import org.kie.uberfirebootstrap.client.resources.images.UberfireBootstrapImages;

/**
 * Generic error dialog popup.
 */
public class ErrorPopup extends Popup {

    private VerticalPanel body = new VerticalPanel();

    private static final String WIDTH = 400 + "px";

    private ErrorPopup() {

        setTitle(UberfireBootstrapConstants.INSTANCE.Error());
        setWidth(WIDTH);
        setModal(true);

        body.setWidth("100%");
    }

    @Override
    public Widget getContent() {
        return body;
    }

    private void addMessage(String message,
                            String longMessage) {

        body.clear();

        final String longDescription = longMessage;

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new Image(UberfireBootstrapImages.INSTANCE.validationError()));
        Label msg = new Label(message);
        msg.setStyleName("error-title");
        hp.add(msg);
        body.add(hp);

        final SimplePanel detailPanel = new SimplePanel();
        if (longMessage != null && !"".equals(longMessage)) {
            Button showD = new Button(UberfireBootstrapConstants.INSTANCE.ShowDetail());
            showD.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    detailPanel.clear();
                    VerticalPanel vp = new VerticalPanel();
                    vp.add(new HTML("<hr/>"));

                    ScrollPanel longMessageLabel = new ScrollPanel(new Label(longDescription));
                    longMessageLabel.setWidth(WIDTH);
                    longMessageLabel.setStyleName("error-long-message");
                    vp.add(longMessageLabel);
                    detailPanel.add(vp);
                }
            });
            detailPanel.add(showD);
        }

        detailPanel.setWidth("100%");
        body.add(detailPanel);
        show();
    }

    /**
     * Convenience method to popup the message.
     */
    public static void showMessage(String message) {
        ErrorPopup instance = new ErrorPopup();
        instance.addMessage(message, null);
    }

    public static void showMessage(String message,
                                   String longDescription) {
        ErrorPopup instance = new ErrorPopup();
        instance.addMessage(message, longDescription);
    }
}