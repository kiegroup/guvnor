package org.drools.guvnor.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Uses ext forms to do a prettier layout.
 */
public class PrettyFormLayout extends Composite {

    private VerticalPanel layout = new VerticalPanel();
    private FlexTable     currentTable;
    private String        sectionName;

    public PrettyFormLayout() {
        layout.setWidth( "100%" );
        initWidget( layout );
    }

    public void startSection() {
        this.currentTable = new FlexTable();
    }

    public void startSection(String title) {
        startSection();
        this.sectionName = title;
    }

    public void clear() {
        this.layout.clear();
    }

    public void addHeader(ImageResource img,
                          String name,
                          Image edit) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( new Image( img ) );
        h.add( new HTML( "&nbsp;" ) );
        h.add( new Label( name ) );
        if ( edit != null ) h.add( edit );

        FormPanel f = newForm( null );
        f.setStyleName("guvnor-FormPanel-darkbackground");

        f.add( h );
        layout.add( f );
    }

    public void addHeader(ImageResource img,
                          Widget content) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( new Image( img ) );
        h.add( new HTML( "&nbsp;" ) );
        h.add( content );
        FormPanel f = newForm( null );
        f.setStyleName("guvnor-FormPanel-darkbackground");

        f.add( h );
        layout.add( f );
    }

    private FormPanel newForm(final String hdr) {
        FormPanel fp = new FormPanel();
        fp.setWidth( "100%" );
        fp.addStyleName( "guvnor-FormPanel" );
        if ( hdr != null ) {
            fp.setTitle( hdr );
        }
        return fp;
    }

    public void endSection() {

        FormPanel f = newForm( this.sectionName );

        f.add( this.currentTable );

        this.layout.add( f );
        this.sectionName = null;
    }

    public void addRow(final Widget widget) {
        int row = currentTable.getRowCount();
        currentTable.setWidget( row,
                                0,
                                widget );
        currentTable.getFlexCellFormatter().setColSpan( row,
                                                        0,
                                                        2 );
        //currentTable.getFlexCellFormatter().setStyleName(row, 0, "cw-FlexTable");
    }

    public int addAttribute(String lbl, final Widget categories) {
        
        int row = currentTable.getRowCount();
        currentTable.setWidget( row,
                                0,
                                new Label( lbl ) );
        
        currentTable.setWidget( row,
                                1,
                                categories );
        currentTable.getFlexCellFormatter().setHorizontalAlignment( row,
                                                                    0,
                                                                    HasHorizontalAlignment.ALIGN_RIGHT );
        currentTable.getFlexCellFormatter().setVerticalAlignment( row,
                                                                  0,
                                                                  HasVerticalAlignment.ALIGN_TOP );
        currentTable.getFlexCellFormatter().setStylePrimaryName(row, 0, "kelake-FlexTable");
        
        return row;
    }
    
    public void removeRow(int row) {

        currentTable.removeRow(row);
    }
}
