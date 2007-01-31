package org.drools.brms.client.packages;

import org.drools.brms.client.common.FormStyleLayout;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the package editor and viewer for package configuration.
 * 
 * @author Michael Neale
 */
public class PackageEditor extends FormStyleLayout {

    private String name;

    public PackageEditor(String name) {
        this.name = name;
        
        setStyleName( "editable-Surface" );
        
        setHeight( "100%" );
        setWidth( "100%" );
        
        addHeader( "images/package_large.png", this.name );
        
        addAttribute( "Description:", description() );
        addAttribute( "Header:", header() );
        addAttribute( "External URI:", externalURI() );
        
        
    }

    private Widget externalURI() {
        return new TextBox();
    }

    private Widget header() {
        
        final TextArea area = new TextArea();
        area.setWidth( "100%" );
        area.setVisibleLines( 4 );
        
        area.setCharacterWidth( 52 );
        
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( area );

        VerticalPanel vert = new VerticalPanel();

        Image max = new Image("images/max_min.gif");
        max.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if (area.getVisibleLines() != 32) {
                    area.setVisibleLines( 32 );
                } else {
                    area.setVisibleLines( 4 );
                }
            }
        } );
        max.setTitle( "Increase view area." );
        vert.add( max );

        Image newImport = new Image("images/new_import.gif");
        newImport.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                area.setText( area.getText(  ) + "\n" + 
                              "import <your class here>");
            }
        });
        vert.add( newImport );
        newImport.setTitle( "Add a new Type/Class import to the package." );
        
        Image newGlobal = new Image("images/new_global.gif");
        newGlobal.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                area.setText( area.getText() + "\n" + 
                              "global <your class here> <variable name>");
            }
        });
        newGlobal.setTitle( "Add a new global variable declaration." );
        vert.add( newGlobal );
        
        panel.add( vert );
        return panel;
    }

    private HorizontalPanel expandableTextArea(final TextArea area) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( area );
        
        Image max = new Image("images/max_min.gif");
        max.setTitle( "Increase view area" );
        
        panel.add( max );
        max.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                if (area.getVisibleLines() != 32) {
                    area.setVisibleLines( 32 );
                } else {
                    area.setVisibleLines( 4 );
                }
            }
        } );
        return panel;
    }

    private Widget description() {
        TextArea area = new TextArea();
        area.setWidth( "100%" );
        area.setVisibleLines( 4 );
        
        area.setCharacterWidth( 52 );
        
        return expandableTextArea( area );
    }
    
}
