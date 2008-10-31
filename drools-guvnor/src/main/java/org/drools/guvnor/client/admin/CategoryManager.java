package org.drools.guvnor.client.admin;
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



import org.drools.guvnor.client.categorynav.CategoryEditor;
import org.drools.guvnor.client.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.categorynav.CategorySelectHandler;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This controls category administration.
 * @author Michael Neale
 */
public class CategoryManager extends Composite {

    public VerticalPanel layout = new VerticalPanel();
    //public String selectedPath;
    private CategoryExplorerWidget explorer;

    public CategoryManager() {

        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader("images/edit_category.gif", new HTML("<b>Edit categories</b>"));
        form.startSection("Categories aid in managing large numbers of rules/assets. A shallow hierarchy is recommented.");

        explorer = new CategoryExplorerWidget(new CategorySelectHandler() {
            public void selected(String sel) {
                //don't need this here as we don't do anything on select in this spot
            }
         });
        SimplePanel editable = new SimplePanel();
        editable.add( explorer );

        form.addAttribute( "Current categories:", editable );

        HorizontalPanel actions = new HorizontalPanel();


        form.addAttribute("", actions);

        Button newCat = new Button( "New category" );
        newCat.setTitle( "Create a new category" );
        newCat.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                CategoryEditor newCat = new CategoryEditor( explorer.getSelectedPath(), new Command() {
					public void execute() {
						explorer.refresh();
					}
                });

                newCat.show();
            }
        } );

        actions.add(newCat);

        Button rename = new Button("Rename selected");
        rename.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				if (!explorer.isSelected()) {
					Window.alert("Please select a category to rename");
					return;
				}
				renameSelected();
			}
        });

        actions.add(rename);


        Button delete = new Button("Delete selected");
        delete.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
            	if (!explorer.isSelected())  {
            		Window.alert("Please select a category to delete.");
            		return;
            	}
                deleteSelected();
            }
        } );
        delete.setTitle( "Deletes the currently selected category. You won't be able to delete if the category is in use." );

        actions.add(delete);

        form.endSection();


        initWidget( form );

    }

    private void renameSelected() {

    	String name = Window.prompt("Please enter the name you would like to change this category to", "");
    	if (name != null) {
	    	RepositoryServiceFactory.getService().renameCategory(explorer.getSelectedPath(), name, new GenericCallback()  {
				public void onSuccess(Object data) {
					Window.alert("Category renamed");
					explorer.refresh();
				}
	    	});
    	}
    }


    private void deleteSelected() {
        if (Window.confirm( "Are you sure you want to delete category: " + explorer.getSelectedPath() )) {
            RepositoryServiceFactory.getService().removeCategory( explorer.getSelectedPath(), new GenericCallback() {

                public void onSuccess(Object data) {
                    explorer.refresh();
                }

            });
        }
    }



}