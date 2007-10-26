package org.drools.brms.client.categorynav;
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



import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This provides a popup for editing a category (name etc).
 * Mainly this is for creating a new category.
 */
public class CategoryEditor extends PopupPanel {

    private String path;
    private FlexTable table = new FlexTable(); //Using this table for the form layout
    private TextBox name = new TextBox();
    private TextArea description = new TextArea();


    /** This is used when creating a new category */
    public CategoryEditor(String catPath) {
        super(true);
        path = catPath;

        table.setWidget( 0, 0, new ImageButton("images/edit_category.gif") );

        table.setWidget( 0, 1, new Label(getTitle( path )));

        table.setWidget( 1, 0, new Label("Category name") );
        table.setWidget( 1, 1, name );

        description.setVisibleLines( 4 );
        table.setWidget( 2, 0, new Label("Description") );
        table.setWidget( 2, 1, description );

        Button ok = new Button("OK");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }

        });

        table.setWidget( 3, 0, ok );

        Button cancel = new Button("Cancel");
        cancel.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                cancel();
            }

        });

        table.setWidget( 3, 1, cancel );

        add( table );
        setStyleName( "ks-popups-Popup" );
    }

    private String getTitle(String catPath) {
        if (catPath == null) {
            return "Create a new top level category.";
        } else {
            return "Create new category under: [" + catPath + "]";
        }
    }

    void ok() {

        AsyncCallback cb = new GenericCallback() {


            public void onSuccess(Object result) {
                if (((Boolean) result).booleanValue()) {
                    hide();
                } else {
                    ErrorPopup.showMessage( "Category was not successfully created. ");

                }
            }
        };

        if ("".equals(this.name.getText())) {
            ErrorPopup.showMessage( "Can't have an empty category name." );
        } else {
            RepositoryServiceFactory.getService().createCategory( path, name.getText(), description.getText(), cb );

        }
    }

    void cancel() {
        hide();
    }
}