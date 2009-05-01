package org.drools.guvnor.client.common;
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



import org.drools.guvnor.client.rpc.DetailedSerializableException;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.WindowListener;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.layout.VerticalLayout;


/**
 * Generic error dialog popup.
 */
public class ErrorPopup  {

    public static ErrorPopup instance = null;
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private VerticalPanel body;
    //new Image("images/error_dialog.png")



    private ErrorPopup(String message, String longMessage) {

    	Window w = new Window();
    	w.setTitle(constants.Error());
    	w.setWidth(400);
    	//w.setHeight((longMessage != null) ? 300 : 150);
    	w.setModal(true);
    	w.setShadow(true);
    	w.setClosable(true);
    	w.setPlain(true);

    	w.setLayout(new VerticalLayout());
        body = new VerticalPanel();


        addMessage(message, longMessage);

        body.setWidth("100%");
        w.add(body);


        

        w.show();


        w.addListener(new WindowListenerAdapter() {
            @Override
            public void onDeactivate(Window window) {
                instance = null;
            }
        });



    }

    private void addMessage(String message, String longMessage) {
        if (message.contains("ItemExistsException")) {    //NON-NLS
            longMessage = message;
            message = constants.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

        }

        final String longDescription = longMessage;

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new Image("images/validation_error.gif"));
        Label msg = new Label(message);
        msg.setStyleName("error-title");
        hp.add(msg);
        body.add(hp);


        final SimplePanel detailPanel = new SimplePanel();
        if (longMessage != null && !"".equals(longMessage)) {
	        Button showD = new Button(constants.ShowDetail());
	        showD.addListener(new ButtonListenerAdapter() {
				public void onClick(Button button, EventObject e) {
					detailPanel.clear();
                    VerticalPanel vp = new VerticalPanel();
                    vp.add(new HTML("<hr/>"));

                    Label lng = new Label(longDescription);
                    lng.setStyleName("error-long-message");
                    vp.add(lng);
                    detailPanel.add(vp);
				}
	        });
	        detailPanel.add(showD);
        }

        detailPanel.setWidth("100%");
        body.add(detailPanel);
    }


    /** Convenience method to popup the message. */
    public static void showMessage(String message) {
        if (instance != null) {
            instance.addMessage(message, null);
        } else {
            instance = new ErrorPopup(message, null);
        }

        LoadingPopup.close();
    }

    /**
     * For showing a more detailed report.
     */
    public static void showMessage(DetailedSerializableException exception) {

        if (instance != null) {
            instance.addMessage(exception.getMessage(), exception.getLongDescription());
        } else {
            instance = new ErrorPopup(exception.getMessage(), exception.getLongDescription());    
        }

        LoadingPopup.close();
    }






}