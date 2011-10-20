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

package org.drools.guvnor.client.widgets;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.widgets.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.widgets.categorynav.CategorySelectHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is a viewer/editor for categories.
 * It will show a list of categories currently applicable, and allow you to
 * remove/add to them.
 *
 * It is intended to work with the meta data form.
 */
public class AssetCategoryEditor extends DirtyableComposite {

    private Constants          constants = GWT.create( Constants.class );
    private static Images      images    = GWT.create( Images.class );

    private MetaData           data;
    private DirtyableFlexTable layout    = new DirtyableFlexTable();
    private FlexTable          list;
    private boolean            readOnly;

    /**
     * @param d The meta data.
     * @param readOnly If it is to be non editable.
     */
    public AssetCategoryEditor(MetaData d,
                               boolean readOnly) {
        this.data = d;

        list = new FlexTable();
        this.readOnly = readOnly;
        loadData( list );
        list.setStyleName( "rule-List" );
        layout.setWidget( 0,
                          0,
                          list );

        if ( !readOnly ) {
            doActions();
        }

        initWidget( layout );
    }

    private void doActions() {
        VerticalPanel actions = new VerticalPanel();
        Image add = new ImageButton( images.newItem() );
        add.setTitle( constants.AddANewCategory() );

        add.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                doOKClick();
            }
        } );

        actions.add( add );
        layout.setWidget( 0,
                          1,
                          actions );

    }

    protected void removeCategory(int idx) {
        data.removeCategory( idx );
        makeDirty();
        resetBox();
    }

    private void resetBox() {
        list = new FlexTable();
        list.setStyleName( "rule-List" );
        layout.setWidget( 0,
                          0,
                          list );
        loadData( list );
        makeDirty();
    }

    private void loadData(FlexTable list) {
        for ( int i = 0; i < data.getCategories().length; i++ ) {
            final int idx = i;

            list.setWidget( i,
                            0,
                            new SmallLabel( data.getCategories()[i] ) );
            if ( !readOnly ) {

                Image del = new ImageButton( images.trash() );
                del.setTitle( constants.RemoveThisCategory() );
                del.addClickHandler( new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        removeCategory( idx );
                    }
                } );
                list.setWidget( i,
                                1,
                                del );
            }
        }
    }

    /** Handles the OK click on the selector popup */
    private void doOKClick() {

        CategorySelector sel = new CategorySelector();
        sel.show();
    }

    /**
     * Appy the change (selected path to be added).
     */
    public void addToCategory(String selectedPath) {

        data.addCategory( selectedPath );
        resetBox();
    }

    /**
     * This is a popup that allows you to select a category to add to the asset.
     */
    class CategorySelector extends FormStylePopup {

        public Button                  ok = new Button( constants.OK() );
        private CategoryExplorerWidget selector;
        public String                  selectedPath;

        public CategorySelector() {
            setTitle( constants.SelectCategoryToAdd() );
            VerticalPanel vert = new VerticalPanel();

            selector = new CategoryExplorerWidget( new CategorySelectHandler() {
                public void selected(String sel) {
                    selectedPath = sel;
                }

            } );

            vert.add( selector );
            vert.add( ok );

            addRow( vert );

            ok.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if ( selectedPath != null && !"".equals( selectedPath ) ) {
                        addToCategory( selectedPath );
                    }
                    hide();
                }
            } );

        }

    }
}
