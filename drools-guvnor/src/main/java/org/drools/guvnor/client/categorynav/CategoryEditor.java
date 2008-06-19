package org.drools.guvnor.client.categorynav;
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



import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

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
public class CategoryEditor extends FormStylePopup {

    private String path;
    private TextBox name = new TextBox();
    private TextArea description = new TextArea();


    /** This is used when creating a new category */
    public CategoryEditor(String catPath) {
    	super("images/edit_category.gif", getTitle(catPath));
        path = catPath;

        addAttribute("Category name", name);

        Button ok = new Button("OK");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }

        });
        addAttribute("", ok);
    }

    private static String getTitle(String catPath) {
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