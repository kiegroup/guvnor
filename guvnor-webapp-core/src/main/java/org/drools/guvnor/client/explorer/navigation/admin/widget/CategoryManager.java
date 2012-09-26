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

import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.CategoryService;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.widgets.categorynav.CategoryEditor;
import org.drools.guvnor.client.widgets.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.widgets.categorynav.CategorySelectHandler;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;

/**
 * This controls category administration.
 */
@Dependent
@WorkbenchScreen(identifier = "categoryManager")
public class CategoryManager extends Composite {

    public VerticalPanel           layout    = new VerticalPanel();
    //public String selectedPath;
    private CategoryExplorerWidget explorer;

    public CategoryManager() {

        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( GuvnorImages.INSTANCE.EditCategories(),
                        new HTML( ConstantsCore.INSTANCE.EditCategories() ) );
        form.startSection( ConstantsCore.INSTANCE.CategoriesPurposeTip() );

        explorer = new CategoryExplorerWidget( new CategorySelectHandler() {
            public void selected(String sel) {
                //don't need this here as we don't do anything on select in this spot
            }
        } );
        SimplePanel editable = new SimplePanel();
        editable.add( explorer );

        form.addAttribute( ConstantsCore.INSTANCE.CurrentCategories(),
                           editable );

        HorizontalPanel actions = new HorizontalPanel();

        form.addAttribute( "",
                           actions );

        Button newCat = new Button( ConstantsCore.INSTANCE.NewCategory() );
        newCat.setTitle( ConstantsCore.INSTANCE.CreateANewCategory() );
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

        Button rename = new Button( ConstantsCore.INSTANCE.RenameSelected() );
        rename.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( !explorer.isSelected() ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseSelectACategoryToRename() );
                    return;
                }
                renameSelected();
            }
        } );

        actions.add( rename );

        Button delete = new Button( ConstantsCore.INSTANCE.DeleteSelected() );
        delete.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( !explorer.isSelected() ) {
                    Window.alert( ConstantsCore.INSTANCE.PleaseSelectACategoryToDelete() );
                    return;
                }
                deleteSelected();
            }
        } );
        delete.setTitle( ConstantsCore.INSTANCE.DeleteSelectedCat() );

        actions.add( delete );

        form.endSection();

        initWidget( form );

    }

    private void renameSelected() {

        String name = Window.prompt( ConstantsCore.INSTANCE.CategoryNewNamePleaseEnter(),
                                     "" );
        if ( name != null ) {
            CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
            categoryService.renameCategory( explorer.getSelectedPath(),
                                                                  name,
                                                                  new GenericCallback<java.lang.Void>() {
                                                                      public void onSuccess(Void v) {
                                                                          Window.alert( ConstantsCore.INSTANCE.CategoryRenamed() );
                                                                          explorer.refresh();
                                                                      }
                                                                  } );
        }
    }

    private void deleteSelected() {
        if ( Window.confirm( ConstantsCore.INSTANCE.AreYouSureYouWantToDeleteCategory() + explorer.getSelectedPath() ) ) {
            CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
            categoryService.removeCategory( explorer.getSelectedPath(),
                                                                  new GenericCallback<java.lang.Void>() {

                                                                      public void onSuccess(Void v) {
                                                                          explorer.refresh();
                                                                      }

                                                                  } );
        }
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return this;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.CategoryManager();
    }
}
