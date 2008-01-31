package org.drools.brms.client.common;
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



import org.drools.brms.client.rpc.DetailedSerializableException;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.ButtonConfig;
import com.gwtext.client.widgets.LayoutDialog;
import com.gwtext.client.widgets.LayoutDialogConfig;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormConfig;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.ContentPanel;
import com.gwtext.client.widgets.layout.LayoutRegionConfig;


/**
 * Generic error dialog popup.
 */
public class ErrorPopup  {

    public static ErrorPopup instance = null;
    //new Image("images/error_dialog.png")


    private ErrorPopup(final String message, final String longMessage) {

      //create and configure layout dialog
        final LayoutDialog dialog = new LayoutDialog(new LayoutDialogConfig() {
        	{
        		setTitle("Error");
        		setModal(true);
        		setWidth(500);
        		setHeight((longMessage != null) ? 500 : 150);
        		setShadow(true);
        		//setMinHeight(300);
        		//setMinHeight(300);
        	}
        }, new LayoutRegionConfig());


        //another way to add button
        dialog.addButton(new Button("OK", new ButtonConfig() {
        	{
        		setText("Cancel");
        		setButtonListener(new ButtonListenerAdapter() {
        			public void onClick(Button button, EventObject e) {
        				dialog.hide();
        			}
        		});
        	}
        }));

        //add content to the center region
        BorderLayout layout = dialog.getLayout();
        ContentPanel contentPanel = new ContentPanel();


        VerticalPanel vp = new VerticalPanel();
        if (longMessage == null) {
        	vp.add(new HTML("<image src='images/error_dialog.png'/>&nbsp;<strong><b>" + message +"</b></strong>"));
        } else {
        	vp.add(new HTML("<image src='images/error_dialog.png'/>&nbsp;<strong><b>" + message +"</b></strong><hr/>"));
        }

        final SimplePanel detailPanel = new SimplePanel();
        if (longMessage != null && !"".equals(longMessage)) {
	        Button showD = new Button("Show detail");
	        showD.addButtonListener(new ButtonListenerAdapter() {
				public void onClick(Button button, EventObject e) {
					detailPanel.clear();
					detailPanel.add(new HTML("<small>" + longMessage + "</small>"));

				}
	        });
	        detailPanel.add(showD);
        }
        vp.setWidth("100%");
        vp.add(detailPanel);
        contentPanel.add(vp);

        layout.add(contentPanel);

        dialog.show();

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