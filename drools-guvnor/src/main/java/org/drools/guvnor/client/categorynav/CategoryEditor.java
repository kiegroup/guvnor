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
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

/**
 * This provides a popup for editing a category (name etc).
 * Mainly this is for creating a new category.
 */
public class CategoryEditor extends FormStylePopup {

    private String path;
    private TextBox name = new TextBox();
    private TextArea description = new TextArea();
	private Command refresh;
    private Constants constants = ((Constants) GWT.create(Constants.class));


    public CategoryEditor(String catPath, Command refresh) {
    	this(catPath);
    	this.refresh = refresh;
    }

    /** This is used when creating a new category */
    public CategoryEditor(String catPath) {
    	//super("images/edit_category.gif", getTitle(catPath));
        super.setTitle(getTitle(catPath));
        path = catPath;

        addAttribute(constants.CategoryName(), name);

        Button ok = new Button(constants.OK());
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget arg0) {
                ok();
            }

        });
        addAttribute("", ok);
    }

    private static String getTitle(String catPath) {
        if (catPath == null) {
            return ((Constants) GWT.create(Constants.class)).CreateANewTopLevelCategory();
        } else {
            return Format.format(((Constants) GWT.create(Constants.class)).CreateNewCategoryUnder0(), catPath);
        }
    }

    void ok() {

        AsyncCallback cb = new GenericCallback() {
            public void onSuccess(Object result) {
                if (((Boolean) result).booleanValue()) {
                	if (refresh != null) {
                		refresh.execute();
                	}
                    hide();
                } else {
                    ErrorPopup.showMessage(constants.CategoryWasNotSuccessfullyCreated());

                }
            }
        };

        if ("".equals(this.name.getText())) {
            ErrorPopup.showMessage(constants.CanNotHaveAnEmptyCategoryName());
        } else {
            RepositoryServiceFactory.getService().createCategory( path, name.getText(), description.getText(), cb );

        }
    }

    void cancel() {
        hide();
    }
}