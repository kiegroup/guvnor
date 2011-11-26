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

package org.drools.guvnor.client.widgets.categorynav;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This provides a popup for editing a category (name etc).
 * Mainly this is for creating a new category.
 */
public class CategoryEditor extends FormStylePopup {

    private String    path;
    private TextBox   name        = new TextBox();
    private TextArea  description = new TextArea();
    private Command   refresh;
    private Constants constants   = ((Constants) GWT.create( Constants.class ));

    public CategoryEditor(String catPath,
                          Command refresh) {
        this( catPath );
        this.refresh = refresh;
    }

    /** This is used when creating a new category */
    public CategoryEditor(String catPath) {
        super.setTitle( getTitle( catPath ) );
        path = catPath;

        addAttribute( constants.CategoryName(),
                      name );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                ok();
            }

        } );
        addAttribute( "",
                      ok );
    }

    private static String getTitle(String catPath) {
        if ( catPath == null ) {
            return ((Constants) GWT.create( Constants.class )).CreateANewTopLevelCategory();
        } else {
            return ((Constants) GWT.create( Constants.class )).CreateNewCategoryUnder0(catPath);
        }
    }

    void ok() {

        AsyncCallback<java.lang.Boolean> cb = new GenericCallback<java.lang.Boolean>() {
            public void onSuccess(Boolean booleanValue) {
                if ( booleanValue ) {
                    if ( refresh != null ) {
                        refresh.execute();
                    }
                    hide();
                } else {
                    ErrorPopup.showMessage( constants.CategoryWasNotSuccessfullyCreated() );

                }
            }
        };

        if ( "".equals( this.name.getText() ) ) {
            ErrorPopup.showMessage( constants.CanNotHaveAnEmptyCategoryName() );
        } else {
            RepositoryServiceFactory.getCategoryService().createCategory( path,
                                                                  name.getText(),
                                                                  description.getText(),
                                                                  cb );

        }
    }

    void cancel() {
        hide();
    }
}
