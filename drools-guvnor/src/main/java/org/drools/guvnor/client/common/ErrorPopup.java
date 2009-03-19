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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.VerticalLayout;


/**
 * Generic error dialog popup.
 */
public class ErrorPopup  {

    public static ErrorPopup instance = null;
    private Constants constants = ((Constants) GWT.create(Constants.class));
    //new Image("images/error_dialog.png")


    private ErrorPopup(String message, String longMessage) {

    	Window w = new Window();
    	w.setTitle(constants.Error());
    	w.setWidth(400);
    	w.setHeight((longMessage != null) ? 300 : 150);
    	w.setModal(true);
    	w.setShadow(true);
    	w.setClosable(true);
    	w.setPlain(true);

    	w.setLayout(new VerticalLayout());

        if (message.contains("ItemExistsException")) {    //NON-NLS
            longMessage = constants.YouMightNeedToBeABitMoreImaginative() + "\n" + message;
            message = constants.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

        }

        final String longDescription = longMessage;
        VerticalPanel vp = new VerticalPanel();
        if (longMessage == null) {
        	vp.add(new HTML("<image src='images/error_dialog.png'/>&nbsp;<strong><b>" + message +"</b></strong>"));
        } else {
        	vp.add(new HTML("<image src='images/error_dialog.png'/>&nbsp;<strong><b>" + message +"</b></strong><hr/>"));
        }

        final SimplePanel detailPanel = new SimplePanel();
        if (longMessage != null && !"".equals(longMessage)) {
	        Button showD = new Button(constants.ShowDetail());
	        showD.addListener(new ButtonListenerAdapter() {
				public void onClick(Button button, EventObject e) {
					detailPanel.clear();
					detailPanel.add(new SmallLabel(longDescription));

				}
	        });
	        detailPanel.add(showD);
        }
        vp.setWidth("100%");
        detailPanel.setWidth("100%");
        vp.add(detailPanel);
        w.add(vp);


        w.show();

    }







    /** Convenience method to popup the message. */
    public static void showMessage(String message) {
    	new ErrorPopup(message, null);
    }

    /**
     * For showing a more detailed report.
     */
    public static void showMessage(DetailedSerializableException exception) {
        new ErrorPopup(exception.getMessage(), exception.getLongDescription());
        LoadingPopup.close();
    }






}