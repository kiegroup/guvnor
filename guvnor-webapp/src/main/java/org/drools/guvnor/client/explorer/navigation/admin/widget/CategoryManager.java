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

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.widgets.categorynav.CategoryEditor;
import org.drools.guvnor.client.widgets.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.widgets.categorynav.CategorySelectHandler;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * This controls category administration.
 */
public class CategoryManager extends Composite {

    private static Images          images    = (Images) GWT.create( Images.class );
    private Constants              constants = ((Constants) GWT.create( Constants.class ));

    public VerticalPanel           layout    = new VerticalPanel();
    //public String selectedPath;
    private CategoryExplorerWidget explorer;

    public CategoryManager() {

        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( images.editCategory(),
                        new HTML( constants.EditCategories() ) );
        form.startSection( constants.CategoriesPurposeTip() );

        explorer = new CategoryExplorerWidget( new CategorySelectHandler() {
            public void selected(String sel) {
                //don't need this here as we don't do anything on select in this spot
            }
        } );
        SimplePanel editable = new SimplePanel();
        editable.add( explorer );

        form.addAttribute( constants.CurrentCategories(),
                           editable );

        HorizontalPanel actions = new HorizontalPanel();

        form.addAttribute( "",
                           actions );

        Button newCat = new Button( constants.NewCategory() );
        newCat.setTitle( constants.CreateANewCategory() );
        newCat.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                CategoryEditor newCat = new CategoryEditor( explorer.getSelectedPath(),
                                                            new Command() {
                                                                public void execute() {
                                                                    explorer.refresh();
                                                                }
                                                            } );

                newCat.show();
            }
        } );

        actions.add( newCat );

        Button rename = new Button( constants.RenameSelected() );
        rename.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( !explorer.isSelected() ) {
                    Window.alert( constants.PleaseSelectACategoryToRename() );
                    return;
                }
                renameSelected();
            }
        } );

        actions.add( rename );

        Button delete = new Button( constants.DeleteSelected() );
        delete.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( !explorer.isSelected() ) {
                    Window.alert( constants.PleaseSelectACategoryToDelete() );
                    return;
                }
                deleteSelected();
            }
        } );
        delete.setTitle( constants.DeleteSelectedCat() );

        actions.add( delete );

        form.endSection();

        initWidget( form );

    }

    private void renameSelected() {

        String name = Window.prompt( constants.CategoryNewNamePleaseEnter(),
                                     "" );
        if ( name != null ) {
            RepositoryServiceFactory.getCategoryService().renameCategory( explorer.getSelectedPath(),
                                                                  name,
                                                                  new GenericCallback<java.lang.Void>() {
                                                                      public void onSuccess(Void v) {
                                                                          Window.alert( constants.CategoryRenamed() );
                                                                          explorer.refresh();
                                                                      }
                                                                  } );
        }
    }

    private void deleteSelected() {
        if ( Window.confirm( constants.AreYouSureYouWantToDeleteCategory() + explorer.getSelectedPath() ) ) {
            RepositoryServiceFactory.getCategoryService().removeCategory( explorer.getSelectedPath(),
                                                                  new GenericCallback<java.lang.Void>() {

                                                                      public void onSuccess(Void v) {
                                                                          explorer.refresh();
                                                                      }

                                                                  } );
        }
    }
}
