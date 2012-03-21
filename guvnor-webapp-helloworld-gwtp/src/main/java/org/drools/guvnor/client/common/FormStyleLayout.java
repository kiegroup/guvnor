package org.drools.guvnor.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
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
 */
public class FormStyleLayout extends Composite {

    private FlexTable         layout      = new FlexTable();
    private FlexCellFormatter formatter   = layout.getFlexCellFormatter();
    private int               numInLayout = 0;

    /**
     * Create a new layout with a header and and icon.
     */
    public FormStyleLayout(ImageResource image,
                           String title) {
        addHeader( image,
                   title );

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
        HTML label = new HTML( "<div class='form-field'>" + lbl + "</div>" );
        layout.setWidget( numInLayout,
                          0,
                          label );
        formatter.setAlignment( numInLayout,
                                0,
                                HasHorizontalAlignment.ALIGN_RIGHT,
                                HasVerticalAlignment.ALIGN_TOP );
        formatter.setWidth(numInLayout, 0, "80");
        layout.setWidget( numInLayout,
                          1,
                          editor );
        formatter.setAlignment( numInLayout,
                                1,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_TOP );

        numInLayout++;
    }

    /** Adds a widget that takes up a whole row. */
    public void addRow(Widget w) {
        layout.setWidget( numInLayout,
                          0,
                          w );
        formatter.setColSpan( numInLayout,
                              0,
                              2 );
        numInLayout++;
    }

    /**
     * Adds a header at the top.
     */
    protected void addHeader(ImageResource image,
                             String title) {
        HTML name = new HTML( "<div class='form-field'><b>" + title + "</b></div>" );
        name.setStyleName( "resource-name-Label" );
        doHeader( image,
                  name );
    }

    private void doHeader(ImageResource imageResource,
                          Widget title) {
        Image image;
        if ( imageResource == null ) {
            image = new Image();
        } else {
            image = new Image( imageResource );
        }
        layout.setWidget( 0,
                          0,
                          image );
        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_TOP );
        layout.setWidget( 0,
                          1,
                          title );
        numInLayout++;
    }

    protected void addHeader(ImageResource image,
                             String title,
                             Widget titleIcon) {
        HTML name = new HTML( "<div class='form-field'><b>" + title + "</b></div>" );
        name.setStyleName( "resource-name-Label" );
        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( name );
        horiz.add( titleIcon );
        doHeader( image,
                  horiz );

    }

    public void setFlexTableWidget(int row,
                                   int col,
                                   Widget widget) {
        layout.setWidget( row,
                          col,
                          widget );
    }

    public int getNumAttributes() {
        return numInLayout;
    }

}
