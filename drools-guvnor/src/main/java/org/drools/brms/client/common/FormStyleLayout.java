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



import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This form style class is to be extended to provide
 * "form style" dialogs (eg in a popup).
 *
 * @author Michael Neale
 */
public class FormStyleLayout extends DirtyableComposite {

    private DirtyableFlexTable layout = new DirtyableFlexTable();
    private FlexCellFormatter formatter = layout.getFlexCellFormatter();
    private int numInLayout = 0;

    /**
     * Create a new layout with a header and and icon.
     */
    public FormStyleLayout(String image, String title) {
        addHeader( image, title );
        initWidget( layout );
    }

    /** This has no header */
    public FormStyleLayout() {
        initWidget( layout );
    }

    /**
     * Clears the layout table.
     */
    public void clear() {
        numInLayout = 0;
        this.layout.clear();
    }

    /**
     * Add a widget to the "form".
     */
    public void addAttribute(String lbl,
                             Widget editor) {
        HTML label = new HTML("<div class='x-form-field'>" + lbl + "</div>");
        layout.setWidget( numInLayout, 0, label );
        formatter.setAlignment( numInLayout, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP );
        layout.setWidget( numInLayout, 1, editor );
        formatter.setAlignment( numInLayout, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );

        numInLayout++;
    }


    /** Adds a widget that takes up a whole row. */
    public void addRow(Widget w) {
        layout.setWidget( numInLayout, 0, w);
        formatter.setColSpan( numInLayout, 0, 2 );
        numInLayout++;
    }

    /**
     * Adds a header at the top.
     */
    protected void addHeader(String image, String title) {
        HTML name = new HTML("<div class='x-form-field'><b>" + title + "</b></div>");
        name.setStyleName( "resource-name-Label" );
        doHeader( image, name );
    }

    private void doHeader(String image, Widget title) {
        layout.setWidget( 0, 0, new Image(image) );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        layout.setWidget( 0, 1, title );
        numInLayout++;
    }



    protected void addHeader(String image, String title, Widget titleIcon) {
    	HTML name = new HTML("<div class='x-form-field'><b>" + title + "</b></div>");
        name.setStyleName( "resource-name-Label" );
        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( name );
        horiz.add( titleIcon );
        doHeader( image, horiz );

    }

    public void setFlexTableWidget(int row, int col, Widget widget){
        layout.setWidget( row, col, widget );
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    public int getNumAttributes() {
    	return numInLayout;
    }


}